package com.example.sessionvotingappkt.web.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Model for response of Schedule")
data class ScheduleResponse(
    @Schema(description = "The id of the Schedule")
    val id: Long? = null,
    @Schema(description = "The name of the Schedule")
    val name: String
)
