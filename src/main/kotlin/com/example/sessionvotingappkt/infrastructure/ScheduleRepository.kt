package com.example.sessionvotingappkt.infrastructure

import com.example.sessionvotingappkt.infrastructure.data.Schedule
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleRepository : JpaRepository<Schedule, Long>