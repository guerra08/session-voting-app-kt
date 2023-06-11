package com.example.sessionvotingappkt.infrastructure.data

import com.example.sessionvotingappkt.util.isBefore
import com.example.sessionvotingappkt.web.dto.response.SessionResponse
import jakarta.persistence.*
import java.time.Instant
import java.time.temporal.ChronoUnit

@Table(name = "sessions")
@Entity
class Session(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    var id: Long? = null,
    @Column(name = "duration_minutes")
    val durationMinutes: Int,
    @Column(name = "start_time")
    val startTime: Instant,
    @Column(name = "close_time")
    var closeTime: Instant? = null,
    @Column(name = "is_active")
    var isActive: Boolean,
    @Column(name = "id_schedule")
    val idSchedule: Long,
    @ManyToOne
    @JoinColumn(name = "id_schedule", insertable = false, updatable = false)
    val schedule: Schedule? = null
)

fun Session.close() {
    this.isActive = false
    this.closeTime = Instant.now()
}

fun Session.toResponse() =
    SessionResponse(
        this.id,
        this.durationMinutes,
        this.startTime,
        this.startTime.plus(this.durationMinutes.toLong(), ChronoUnit.MINUTES)
    )

fun Session.isExpired() =
    this.startTime.plus(this.durationMinutes.toLong(), ChronoUnit.MINUTES) isBefore Instant.now()