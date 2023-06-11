package com.example.sessionvotingappkt.web.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

@Schema(description = "Model to open a Session for a given Schedule")
data class CreateSessionRequest(
    @Schema(description = "The duration of the Session. Value is in minutes, with minimum value 1")
    @get:Min(1) val durationMinutes: Int = 1
)
