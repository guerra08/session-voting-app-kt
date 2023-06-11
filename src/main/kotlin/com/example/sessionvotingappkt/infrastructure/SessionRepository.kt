package com.example.sessionvotingappkt.infrastructure

import com.example.sessionvotingappkt.infrastructure.data.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SessionRepository : JpaRepository<Session, Long> {

    @Query("SELECT s FROM Session s WHERE s.isActive = true AND s.idSchedule = :scheduleId")
    fun findFirstByIsActiveTrueAndScheduleId(scheduleId: Long): Session?

    @Query("SELECT s FROM Session s WHERE s.isActive = true")
    fun findAllMarkedAsActive(): List<Session>

}