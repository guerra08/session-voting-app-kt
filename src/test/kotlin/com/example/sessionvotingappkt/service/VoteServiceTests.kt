package com.example.sessionvotingappkt.service

import com.example.sessionvotingappkt.exception.*
import com.example.sessionvotingappkt.external.AssociateValidation
import com.example.sessionvotingappkt.infrastructure.ScheduleReport
import com.example.sessionvotingappkt.infrastructure.SessionReport
import com.example.sessionvotingappkt.infrastructure.VoteRepository
import com.example.sessionvotingappkt.infrastructure.data.Vote
import com.example.sessionvotingappkt.service.impl.DefaultVoteService
import com.example.sessionvotingappkt.util.VotingOptions
import com.example.sessionvotingappkt.web.dto.request.CreateVoteRequest
import com.example.sessionvotingappkt.web.dto.response.SessionResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.temporal.ChronoUnit

class VoteServiceTests {

    private val sessionService: SessionService = mock()
    private val scheduleService: ScheduleService = mock()
    private val associateValidation: AssociateValidation = mock()
    private val voteRepository: VoteRepository = mock()

    private val sut: VoteService =
        DefaultVoteService(sessionService, scheduleService, associateValidation, voteRepository)

    @Test
    fun `should throw if is cpf validation fails`() {
        val request = CreateVoteRequest(cpf = "123456789", userId = 1L, option = VotingOptions.YES)
        whenever(associateValidation.validateAssociateCpf(request.cpf)).thenReturn(false)

        val ex = assertThrows<BadVoteRequestException> { sut.voteForSessionOfSchedule(1L, request) }

        assertEquals("Invalid CPF being used for voting", ex.message)

        verify(associateValidation, times(1)).validateAssociateCpf(request.cpf)
    }

    @Test
    fun `should throw if schedule does not exists`() {
        val request = CreateVoteRequest(cpf = "123456789", userId = 1L, option = VotingOptions.YES)
        whenever(associateValidation.validateAssociateCpf(request.cpf)).thenReturn(true)
        whenever(scheduleService.existsById(1L)).thenReturn(false)

        assertThrows<ScheduleNotFoundException> { sut.voteForSessionOfSchedule(1L, request) }

        verify(associateValidation, times(1)).validateAssociateCpf(request.cpf)
        verify(scheduleService, times(1)).existsById(1L)
    }

    @Test
    fun `should throw if session is invalid`() {
        val request = CreateVoteRequest(cpf = "123456789", userId = 1L, option = VotingOptions.YES)
        whenever(associateValidation.validateAssociateCpf(request.cpf)).thenReturn(true)
        whenever(scheduleService.existsById(1L)).thenReturn(true)
        whenever(sessionService.findCurrentOfSchedule(1L)).thenReturn(null)

        val ex = assertThrows<SessionConflictException> { sut.voteForSessionOfSchedule(1L, request) }

        assertEquals("Session of Schedule has already closed or does not exist", ex.message)

        verify(associateValidation, times(1)).validateAssociateCpf(request.cpf)
        verify(scheduleService, times(1)).existsById(1L)
        verify(sessionService, times(1)).findCurrentOfSchedule(1L)
    }

    @Test
    fun `should throw if user has already voted`() {
        val request = CreateVoteRequest(cpf = "123456789", userId = 1L, option = VotingOptions.YES)
        whenever(associateValidation.validateAssociateCpf(request.cpf)).thenReturn(true)
        whenever(scheduleService.existsById(1L)).thenReturn(true)
        whenever(sessionService.findCurrentOfSchedule(1L)).thenReturn(
            SessionResponse(
                1L,
                10,
                Instant.now(),
                Instant.now().plus(10, ChronoUnit.MINUTES)
            )
        )
        whenever(voteRepository.existsByIdSessionAndIdAssociate(1L, request.userId))
            .thenReturn(true)

        val ex = assertThrows<VoteConflictException> { sut.voteForSessionOfSchedule(1L, request) }

        assertEquals("A vote for this user has already been registered.", ex.message)

        verify(associateValidation, times(1)).validateAssociateCpf(request.cpf)
        verify(scheduleService, times(1)).existsById(1L)
        verify(sessionService, times(1)).findCurrentOfSchedule(1L)
        verify(voteRepository, times(1)).existsByIdSessionAndIdAssociate(1L, request.userId)
    }

    @Test
    fun `should compute vote`() {
        val request = CreateVoteRequest(cpf = "123456789", userId = 1L, option = VotingOptions.YES)
        whenever(associateValidation.validateAssociateCpf(request.cpf)).thenReturn(true)
        whenever(scheduleService.existsById(1L)).thenReturn(true)
        whenever(sessionService.findCurrentOfSchedule(1L)).thenReturn(
            SessionResponse(
                1L,
                10,
                Instant.now(),
                Instant.now().plus(10, ChronoUnit.MINUTES)
            )
        )
        whenever(voteRepository.existsByIdSessionAndIdAssociate(1L, request.userId))
            .thenReturn(false)
        whenever(voteRepository.save(any())).thenReturn(
            Vote(
                1L,
                request.userId,
                creationTime = Instant.now(),
                idSession = 1L,
                option = request.option.value
            )
        )

        val result = sut.voteForSessionOfSchedule(1L, request)

        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals(VotingOptions.YES, result?.value)

        verify(associateValidation, times(1)).validateAssociateCpf(request.cpf)
        verify(scheduleService, times(1)).existsById(1L)
        verify(sessionService, times(1)).findCurrentOfSchedule(1L)
        verify(voteRepository, times(1)).existsByIdSessionAndIdAssociate(1L, request.userId)
        verify(voteRepository, times(1)).save(any())
    }

    @Test
    fun `should throw if schedule does not exists when getting report for schedule`() {
        whenever(scheduleService.existsById(1L)).thenReturn(false)

        assertThrows<ScheduleNotFoundException> { sut.getReportForSchedule(1L) }

        verify(scheduleService, times(1)).existsById(1L)
    }

    @Test
    fun `should throw if session does not exists when getting report for session`() {
        whenever(sessionService.existsById(1L)).thenReturn(false)

        assertThrows<SessionNotFoundException> { sut.getReportForSession(1L) }

        verify(sessionService, times(1)).existsById(1L)
    }

    @Test
    fun `should return report for session`() {
        whenever(sessionService.existsById(1L)).thenReturn(true)
        whenever(voteRepository.findVoteReportOfSession(1L))
            .thenReturn(listOf(object : SessionReport {
                override fun getOption() = "YES"
                override fun getCount() = 2
            }, object : SessionReport {
                override fun getOption() = "NO"
                override fun getCount() = 1
            }))

        val result = sut.getReportForSession(1L)

        assertEquals(1L, result.sessionId)
        assertEquals(3, result.totalCount)
    }

    @Test
    fun `should return report for schedule`() {
        whenever(scheduleService.existsById(1L)).thenReturn(true)
        whenever(voteRepository.findVoteReportOfSchedule(1L))
            .thenReturn(listOf(object : ScheduleReport {
                override fun getId() = 1L
                override fun getOption() = "YES"
                override fun getCount() = 1

            }, object : ScheduleReport {
                override fun getId() = 1L
                override fun getOption() = "NO"
                override fun getCount() = 2
            }))

        val result = sut.getReportForSchedule(1L)

        assertEquals(1L, result.scheduleId)
    }

}