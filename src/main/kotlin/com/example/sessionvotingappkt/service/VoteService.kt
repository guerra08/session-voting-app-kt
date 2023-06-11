package com.example.sessionvotingappkt.service

import com.example.sessionvotingappkt.web.dto.request.CreateVoteRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleReportResponse
import com.example.sessionvotingappkt.web.dto.response.SessionReportResponse
import com.example.sessionvotingappkt.web.dto.response.VoteResponse

interface VoteService {

    /**
     * Votes for a specific Schedule, with an active Session
     */
    fun voteForSessionOfSchedule(scheduleId: Long, createVoteRequest: CreateVoteRequest): VoteResponse?

    /**
     * Gets the report of votes for a Schedule
     */
    fun getReportForSchedule(scheduleId: Long): ScheduleReportResponse

    /**
     * Gets the report of votes for a Session
     */
    fun getReportForSession(sessionId: Long): SessionReportResponse

}