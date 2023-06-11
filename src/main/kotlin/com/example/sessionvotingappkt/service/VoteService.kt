package com.example.sessionvotingappkt.service

import com.example.sessionvotingappkt.web.dto.request.CreateVoteRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleReportResponse
import com.example.sessionvotingappkt.web.dto.response.SessionReportResponse
import com.example.sessionvotingappkt.web.dto.response.VoteResponse

interface VoteService {

    fun voteForSessionOfSchedule(scheduleId: Long, createVoteRequest: CreateVoteRequest): VoteResponse?

    fun getReportForSchedule(scheduleId: Long): ScheduleReportResponse

    fun getReportForSession(sessionId: Long): SessionReportResponse

}