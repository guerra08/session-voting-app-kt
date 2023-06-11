package com.example.sessionvotingappkt.web.rest

import com.example.sessionvotingappkt.exception.*
import com.example.sessionvotingappkt.service.VoteService
import com.example.sessionvotingappkt.util.VotingOptions
import com.example.sessionvotingappkt.web.dto.request.CreateVoteRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleReportResponse
import com.example.sessionvotingappkt.web.dto.response.SessionReportDetails
import com.example.sessionvotingappkt.web.dto.response.SessionReportResponse
import com.example.sessionvotingappkt.web.dto.response.VoteResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Instant

@WebMvcTest(controllers = [VoteController::class])
class VoteControllerTests(@Autowired val mockMvc: MockMvc) {

    @MockBean
    lateinit var voteService: VoteService

    @Test
    fun `should return bad request if vote request has invalid cpf`() {
        val request = CreateVoteRequest("123456789", 1L, VotingOptions.YES)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/votes/schedule/1")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should return bad request if vote request cpf is invalidated with external service`() {
        val request = CreateVoteRequest("37234355057", 1L, VotingOptions.YES)
        whenever(voteService.voteForSessionOfSchedule(1L, request))
            .thenThrow(BadVoteRequestException("Invalid CPF being used for voting"))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/votes/schedule/1")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should return not found when trying to vote for a non existing schedule`() {
        val request = CreateVoteRequest("37234355057", 1L, VotingOptions.YES)
        whenever(voteService.voteForSessionOfSchedule(1L, request))
            .thenThrow(ScheduleNotFoundException())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/votes/schedule/1")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should return not found when trying to vote for an invalid session`() {
        val request = CreateVoteRequest("37234355057", 1L, VotingOptions.YES)
        whenever(voteService.voteForSessionOfSchedule(1L, request))
            .thenThrow(SessionConflictException("Session of Schedule has already closed or does not exist"))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/votes/schedule/1")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isConflict)
    }

    @Test
    fun `should return not found when trying to vote twice as same user`() {
        val request = CreateVoteRequest("37234355057", 1L, VotingOptions.YES)
        whenever(voteService.voteForSessionOfSchedule(1L, request))
            .thenThrow(VoteConflictException("A vote for this user has already been registered."))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/votes/schedule/1")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isConflict)
    }

    @Test
    fun `should register vote`() {
        val request = CreateVoteRequest("37234355057", 1L, VotingOptions.YES)
        whenever(voteService.voteForSessionOfSchedule(1L, request))
            .thenReturn(VoteResponse(1L, VotingOptions.YES, Instant.now()))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/votes/schedule/1")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `should return report of votes for session`() {
        whenever(voteService.getReportForSession(1L))
            .thenReturn(SessionReportResponse(1L, 10, listOf(SessionReportDetails("YES", 10))))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/votes/result/session/1")
                .contentType("application/json")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should return not found when getting report of non existing session`() {
        whenever(voteService.getReportForSession(1L))
            .thenThrow(SessionNotFoundException())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/votes/result/session/1")
                .contentType("application/json")
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should return report of votes for schedule`() {
        whenever(voteService.getReportForSchedule(1L))
            .thenReturn(
                ScheduleReportResponse(
                    1L,
                    listOf(SessionReportResponse(1L, 10, listOf(SessionReportDetails("NO", 10))))
                )
            )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/votes/result/schedule/1")
                .contentType("application/json")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should return not found when getting report of non existing schedule`() {
        whenever(voteService.getReportForSchedule(1L))
            .thenThrow(ScheduleNotFoundException())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/votes/result/schedule/1")
                .contentType("application/json")
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

}