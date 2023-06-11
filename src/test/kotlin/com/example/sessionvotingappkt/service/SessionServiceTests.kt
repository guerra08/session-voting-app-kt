package com.example.sessionvotingappkt.service

import com.example.sessionvotingappkt.exception.SessionConflictException
import com.example.sessionvotingappkt.infrastructure.SessionRepository
import com.example.sessionvotingappkt.infrastructure.data.Session
import com.example.sessionvotingappkt.service.impl.DefaultSessionService
import com.example.sessionvotingappkt.web.dto.request.CreateSessionRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.any
import org.mockito.Mockito.anyList
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.temporal.ChronoUnit

class SessionServiceTests {

    private val sessionRepository: SessionRepository = mock()
    private val sut: SessionService = DefaultSessionService(sessionRepository)

    @Test
    fun `should not open a session for schedule with a current open session`() {
        whenever(sessionRepository.findFirstByIsActiveTrueAndScheduleId(1L))
            .thenReturn(
                Session(
                    id = 1L,
                    durationMinutes = 10,
                    startTime = Instant.now(),
                    isActive = true,
                    idSchedule = 1L
                )
            )

        val dto = CreateSessionRequest(durationMinutes = 10)

        assertThrows<SessionConflictException> { sut.open(1L, dto) }

        verify(sessionRepository, times(1)).findFirstByIsActiveTrueAndScheduleId(1L)
    }

    @Test
    fun `should open a session for schedule`() {
        whenever(sessionRepository.findFirstByIsActiveTrueAndScheduleId(1L)).thenReturn(null)
        whenever(sessionRepository.save(any()))
            .thenReturn(
                Session(
                    id = 1L,
                    startTime = Instant.now(),
                    isActive = true,
                    idSchedule = 1L,
                    durationMinutes = 10
                )
            )

        val dto = CreateSessionRequest(durationMinutes = 10)

        val result = sut.open(1L, dto)

        assertEquals(1, result.id)
        assertEquals(10, result.durationMinutes)
        assertEquals(result.end, result.start.plus(result.durationMinutes.toLong(), ChronoUnit.MINUTES))
    }

    @Test
    fun `should return that session exists by id`() {
        whenever(sessionRepository.existsById(1L)).thenReturn(true)

        val result = sut.existsById(1L)

        assertTrue(result)
    }

    @Test
    fun `should return that session does not exists by id`() {
        whenever(sessionRepository.existsById(1L)).thenReturn(false)

        val result = sut.existsById(1L)

        assertFalse(result)
    }

    @Test
    fun `should return current active session of schedule`() {
        val now = Instant.now()
        val entity = Session(id = 1L, isActive = true, idSchedule = 1L, durationMinutes = 10, startTime = now)
        whenever(sessionRepository.findFirstByIsActiveTrueAndScheduleId(1L))
            .thenReturn(entity)

        val result = sut.findCurrentOfSchedule(1L)

        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals(10, result?.durationMinutes)
        assertEquals(now, result?.start)
        assertEquals(now.plus(10L, ChronoUnit.MINUTES), result?.end)
    }

    @Test
    fun `should return null if session has expired`() {
        val start = Instant.now().minus(11L, ChronoUnit.MINUTES)
        val entity = Session(id = 1L, isActive = true, idSchedule = 1L, durationMinutes = 10, startTime = start)
        whenever(sessionRepository.findFirstByIsActiveTrueAndScheduleId(1L))
            .thenReturn(entity)

        val result = sut.findCurrentOfSchedule(1L)

        assertNull(result)
    }

    @Test
    fun `should return null if session is not found`() {
        whenever(sessionRepository.findFirstByIsActiveTrueAndScheduleId(1L))
            .thenReturn(null)

        val result = sut.findCurrentOfSchedule(1L)

        assertNull(result)
    }

    @Test
    fun `should close expired sessions`() {
        val start = Instant.now().minus(11L, ChronoUnit.MINUTES)
        val activeSessions = listOf(
            Session(id = 1L, isActive = true, idSchedule = 1L, durationMinutes = 10, startTime = start)
        )
        val closedSessions = listOf(
            Session(
                id = 1L,
                isActive = false,
                idSchedule = 1L,
                durationMinutes = 10,
                startTime = start,
                closeTime = Instant.now()
            )
        )
        whenever(sessionRepository.findAllMarkedAsActive()).thenReturn(activeSessions)
        whenever(sessionRepository.saveAll(anyList())).thenReturn(closedSessions)

        val result = sut.closeExpiredSessions()

        assertEquals(1, result.size)
    }

}