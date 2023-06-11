package com.example.sessionvotingappkt.web.dto.request

import com.example.sessionvotingappkt.util.VotingOptions
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.br.CPF
import org.jetbrains.annotations.NotNull

@Schema(description = "Model to cast a vote in the current Session of a Schedule")
data class CreateVoteRequest(
    @Schema(description = "CPF of the voter")
    @get:CPF val cpf: String,
    @Schema(description = "The user ID of the voter")
    @get:NotNull val userId: Long,
    @Schema(description = "The voting option (YES|NO)")
    @get:NotNull val option: VotingOptions
)
