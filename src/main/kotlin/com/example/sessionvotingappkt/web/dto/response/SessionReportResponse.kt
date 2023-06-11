package com.example.sessionvotingappkt.web.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class SessionReportDetails(
    @Schema(description = "The value / name of the option")
    val option: String,
    @Schema(description = "The total count for that option")
    val totalCount: Int
)

@Schema(description = "Response model for the voting report of a Session")
data class SessionReportResponse(
    @Schema(description = "The id of the Session")
    val sessionId: Long,
    @Schema(description = "The total amount of votes")
    val totalCount: Int,
    @Schema(description = "The detailed amount of votes for each option")
    val details: List<SessionReportDetails>
)