package com.example.sessionvotingappkt.scheduler

import com.example.sessionvotingappkt.service.SessionService
import com.example.sessionvotingappkt.service.VoteService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SessionScheduler(
    val sessionService: SessionService,
    val voteService: VoteService,
    val rabbitTemplate: RabbitTemplate,
    val queue: Queue
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Scheduled(fixedDelay = 1000L)
    fun closeSessionRoutine() {
        val closedSessions = sessionService.closeExpiredSessions()
        if (closedSessions.isNotEmpty()) {
            log.info("Closed expired sessions. Sending notifications to RabbitMQ queue...")
            val sessionsReports = closedSessions.map { voteService.getReportForSession(it.id!!) }
            sessionsReports.forEach {
                rabbitTemplate.convertAndSend(queue.name, it.toString())
            }
        }
    }

}