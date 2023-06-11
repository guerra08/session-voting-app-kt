package com.example.sessionvotingappkt.service

import com.example.sessionvotingappkt.web.dto.request.CreateSessionRequest
import com.example.sessionvotingappkt.web.dto.response.SessionResponse

interface SessionService {

    /**
     * Opens a Session for a Schedule
     */
    fun open(scheduleId: Long, createSessionRequest: CreateSessionRequest): SessionResponse

    /**
     * Finds the current active Session of a Schedule
     */
    fun findCurrentOfSchedule(scheduleId: Long): SessionResponse?

    /**
     * Checks if Session exists
     */
    fun existsById(sessionId: Long): Boolean

    /**
     * Closes the expired Sessions that are currently open
     */
    fun closeExpiredSessions(): List<SessionResponse>

}