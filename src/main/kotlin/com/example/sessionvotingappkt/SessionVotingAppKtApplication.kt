package com.example.sessionvotingappkt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SessionVotingAppKtApplication

fun main(args: Array<String>) {
    runApplication<SessionVotingAppKtApplication>(*args)
}
