package com.example.sessionvotingappkt.web.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Model for response of Session")
data class SessionResponse(
    @Schema(description = "Id of the Session")
    val id: Long?,
    @Schema(description = "The duration of the Session in minutes")
    val durationMinutes: Int,
    @Schema(description = "When the Session started")
    val start: Instant,
    @Schema(description = "When the Session will end")
    val end: Instant
)
