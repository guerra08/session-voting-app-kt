package com.example.sessionvotingappkt.external.impl

import com.example.sessionvotingappkt.external.AssociateValidation
import com.example.sessionvotingappkt.util.CpfValidationResults
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class DefaultAssociateValidation(
    webClientBuilder: WebClient.Builder,
    @Value("\${cpf-validation.enabled:false}") private val cpfValidationEnabled: Boolean,
    @Value("\${cpf-validation.url}") private val cpfValidationBaseUrl: String
) : AssociateValidation {

    private val webClient: WebClient = webClientBuilder.baseUrl(cpfValidationBaseUrl).build()

    override fun validateAssociateCpf(cpf: String): Boolean {
        if (!cpfValidationEnabled) return true

        return try {
            val response = webClient
                .get()
                .uri("/${cpf}")
                .retrieve()
                .toEntity(String::class.java)
                .block()!!
            return response.statusCode == HttpStatusCode.valueOf(200) &&
                    response.body?.equals(CpfValidationResults.ABLE_TO_VOTE.text) == true
        } catch (_: Throwable) {
            false
        }
    }

}
