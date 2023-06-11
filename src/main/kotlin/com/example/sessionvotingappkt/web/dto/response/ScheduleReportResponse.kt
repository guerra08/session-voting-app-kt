package com.example.sessionvotingappkt.web.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response model for the voting report of a Schedule")
data class ScheduleReportResponse(
    @Schema(description = "The id of the Schedule")
    val scheduleId: Long,
    @Schema(description = "The detailed report for each Session of the Schedule")
    val details: List<SessionReportResponse>
)
