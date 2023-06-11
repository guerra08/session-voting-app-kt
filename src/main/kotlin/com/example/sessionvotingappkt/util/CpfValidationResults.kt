package com.example.sessionvotingappkt.util

enum class CpfValidationResults(val text: String) {
    ABLE_TO_VOTE("{\"status\": \"ABLE_TO_VOTE\"}"),
    UNABLE_TO_VOTE("{\"status\": \"UNABLE_TO_VOTE\"}")
}