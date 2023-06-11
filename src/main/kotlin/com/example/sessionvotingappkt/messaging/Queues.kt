package com.example.sessionvotingappkt.messaging

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Queues {

    @Bean
    fun queue(): Queue = Queue("session-voting-results", true)

}