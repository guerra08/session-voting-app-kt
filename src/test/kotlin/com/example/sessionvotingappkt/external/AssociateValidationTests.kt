package com.example.sessionvotingappkt.external

import com.example.sessionvotingappkt.external.impl.DefaultAssociateValidation
import com.example.sessionvotingappkt.util.CpfValidationResults
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient


class AssociateValidationTests {

    @Test
    fun `should not call external service if flag is false`() {
        val sut: AssociateValidation = DefaultAssociateValidation(WebClient.builder(), false, "http://localhost:1234")

        val result = sut.validateAssociateCpf("12345678912")

        assertTrue(result)
    }

    @Test
    fun `should return false if validation fails`() {
        val server = MockWebServer()
        server.start()

        server.enqueue(MockResponse().setResponseCode(404))
        val sut: AssociateValidation = DefaultAssociateValidation(WebClient.builder(), true, server.url("").toString())

        val result = sut.validateAssociateCpf("12345678912")

        server.takeRequest()

        server.shutdown()

        assertFalse(result)
    }

    @Test
    fun `should return true if validation passes`() {
        val server = MockWebServer()
        server.start()

        server.enqueue(MockResponse().setBody(CpfValidationResults.ABLE_TO_VOTE.text).setResponseCode(200))
        val sut: AssociateValidation = DefaultAssociateValidation(WebClient.builder(), true, server.url("").toString())

        val result = sut.validateAssociateCpf("12345678912")

        server.takeRequest()

        server.shutdown()

        assertTrue(result)
    }

    @Test
    fun `should return true if validation fails with status 200`() {
        val server = MockWebServer()
        server.start()

        server.enqueue(MockResponse().setBody(CpfValidationResults.UNABLE_TO_VOTE.text).setResponseCode(200))
        val sut: AssociateValidation = DefaultAssociateValidation(WebClient.builder(), true, server.url("").toString())

        val result = sut.validateAssociateCpf("12345678912")

        server.takeRequest()

        server.shutdown()

        assertFalse(result)
    }

}