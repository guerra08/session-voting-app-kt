package com.example.sessionvotingappkt.web.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Model to create a new Schedule")
data class CreateScheduleRequest(
    @Schema(description = "The name of the Schedule")
    @get:NotBlank val name: String
)
