package com.example.sessionvotingappkt.service.impl

import com.example.sessionvotingappkt.exception.*
import com.example.sessionvotingappkt.external.AssociateValidation
import com.example.sessionvotingappkt.infrastructure.VoteRepository
import com.example.sessionvotingappkt.infrastructure.data.Vote
import com.example.sessionvotingappkt.infrastructure.data.toResponse
import com.example.sessionvotingappkt.service.ScheduleService
import com.example.sessionvotingappkt.service.SessionService
import com.example.sessionvotingappkt.service.VoteService
import com.example.sessionvotingappkt.util.VotingOptions
import com.example.sessionvotingappkt.web.dto.request.CreateVoteRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleReportResponse
import com.example.sessionvotingappkt.web.dto.response.SessionReportDetails
import com.example.sessionvotingappkt.web.dto.response.SessionReportResponse
import com.example.sessionvotingappkt.web.dto.response.VoteResponse
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DefaultVoteService(
    val sessionService: SessionService,
    val scheduleService: ScheduleService,
    val associateValidation: AssociateValidation,
    val voteRepository: VoteRepository
) : VoteService {

    override fun voteForSessionOfSchedule(scheduleId: Long, createVoteRequest: CreateVoteRequest): VoteResponse {
        voteRequestValidation(createVoteRequest)
        if (!scheduleService.existsById(scheduleId)) {
            throw ScheduleNotFoundException()
        }
        val currentSessionId = sessionService.findCurrentOfSchedule(scheduleId)?.id
            ?: throw SessionConflictException("Session of Schedule has already closed or does not exist")
        if (voteRepository.existsByIdSessionAndIdAssociate(currentSessionId, createVoteRequest.userId)) {
            throw VoteConflictException("A vote for this user has already been registered.")
        }
        val entity = voteRepository.save(
            Vote(
                idAssociate = createVoteRequest.userId,
                option = createVoteRequest.option.value,
                idSession = currentSessionId,
                creationTime = Instant.now()
            )
        )
        return entity.toResponse()
    }

    override fun getReportForSchedule(scheduleId: Long): ScheduleReportResponse {
        if (!scheduleService.existsById(scheduleId)) {
            throw ScheduleNotFoundException()
        }

        val result = voteRepository.findVoteReportOfSchedule(scheduleId)
            .groupBy { it.getId() }
            .map {
                val total = it.value.sumOf { report -> report.getCount() }
                SessionReportResponse(
                    sessionId = it.key,
                    totalCount = total,
                    details = it.value.map { report -> SessionReportDetails(report.getOption(), report.getCount()) })
            }
        return ScheduleReportResponse(scheduleId = scheduleId, details = result)
    }

    override fun getReportForSession(sessionId: Long): SessionReportResponse {
        if (!sessionService.existsById(sessionId)) {
            throw SessionNotFoundException()
        }

        val result = voteRepository.findVoteReportOfSession(sessionId)
        val total = result.sumOf { it.getCount() }
        return SessionReportResponse(
            sessionId = sessionId,
            totalCount = total,
            details = result.map { SessionReportDetails(it.getOption(), it.getCount()) })
    }

    private fun voteRequestValidation(createVoteRequest: CreateVoteRequest) {
        if (!VotingOptions.values().contains(createVoteRequest.option)) {
            throw BadVoteRequestException("Invalid voting option [${createVoteRequest.option}]")
        }
        if (!associateValidation.validateAssociateCpf(createVoteRequest.cpf)) {
            throw BadVoteRequestException("Invalid CPF being used for voting")
        }
    }
}