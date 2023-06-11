package com.example.sessionvotingappkt.infrastructure.data

import com.example.sessionvotingappkt.web.dto.response.ScheduleResponse
import jakarta.persistence.*

@Entity
@Table(name = "schedules")
class Schedule(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    var id: Long? = null,
    @Column(name = "name")
    val name: String,
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_schedule")
    val sessions: List<Session> = mutableListOf()
)

fun Schedule.toResponse() =
    ScheduleResponse(this.id, this.name)