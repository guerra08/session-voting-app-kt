package com.example.sessionvotingappkt.service.impl

import com.example.sessionvotingappkt.exception.SessionConflictException
import com.example.sessionvotingappkt.infrastructure.SessionRepository
import com.example.sessionvotingappkt.infrastructure.data.Session
import com.example.sessionvotingappkt.infrastructure.data.close
import com.example.sessionvotingappkt.infrastructure.data.isExpired
import com.example.sessionvotingappkt.infrastructure.data.toResponse
import com.example.sessionvotingappkt.service.SessionService
import com.example.sessionvotingappkt.web.dto.request.CreateSessionRequest
import com.example.sessionvotingappkt.web.dto.response.SessionResponse
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DefaultSessionService(
    private val sessionRepository: SessionRepository
) : SessionService {

    override fun open(scheduleId: Long, createSessionRequest: CreateSessionRequest): SessionResponse {
        val currentSession = sessionRepository.findFirstByIsActiveTrueAndScheduleId(scheduleId)

        if (currentSession != null && !currentSession.isExpired()) {
            throw SessionConflictException("An open Session already exists for this Schedule")
        }

        val now = Instant.now()

        var entity = Session(
            startTime = now,
            durationMinutes = createSessionRequest.durationMinutes ?: 1,
            isActive = true,
            idSchedule = scheduleId
        )

        entity = sessionRepository.save(entity)

        return entity.toResponse()
    }

    override fun findCurrentOfSchedule(scheduleId: Long): SessionResponse? {
        val currentSession = sessionRepository.findFirstByIsActiveTrueAndScheduleId(scheduleId)

        return currentSession?.let { session ->
            if (session.isExpired()) return@let null
            session.toResponse()
        }
    }

    override fun existsById(sessionId: Long) =
        sessionRepository.existsById(sessionId)


    override fun closeExpiredSessions(): List<SessionResponse> {
        val markedAsActive = sessionRepository.findAllMarkedAsActive()
        val closed = markedAsActive
            .filter {
                it.isExpired()
            }
            .onEach { it.close() }
        return sessionRepository.saveAll(closed).map { it.toResponse() }
    }
}