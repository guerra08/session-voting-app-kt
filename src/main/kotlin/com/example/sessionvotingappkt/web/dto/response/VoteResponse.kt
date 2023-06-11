package com.example.sessionvotingappkt.web.dto.response

import com.example.sessionvotingappkt.util.VotingOptions
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Model for response of a Vote")
data class VoteResponse(
    @Schema(description = "Id of the Vote")
    val id: Long?,
    @Schema(description = "The option selected for the Vote")
    val value: VotingOptions,
    @Schema(description = "Time of the Vote computation")
    val timestamp: Instant
)
