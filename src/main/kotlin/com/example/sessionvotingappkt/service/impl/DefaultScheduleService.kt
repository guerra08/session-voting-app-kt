package com.example.sessionvotingappkt.service.impl

import com.example.sessionvotingappkt.infrastructure.ScheduleRepository
import com.example.sessionvotingappkt.infrastructure.data.Schedule
import com.example.sessionvotingappkt.infrastructure.data.toResponse
import com.example.sessionvotingappkt.service.ScheduleService
import com.example.sessionvotingappkt.web.dto.request.CreateScheduleRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DefaultScheduleService(private val scheduleRepository: ScheduleRepository) : ScheduleService {
    override fun create(createScheduleDto: CreateScheduleRequest): ScheduleResponse {
        val entity = Schedule(name = createScheduleDto.name)
        return scheduleRepository.save(entity).toResponse()
    }

    override fun findById(id: Long) =
        scheduleRepository.findByIdOrNull(id)?.toResponse()

    override fun existsById(id: Long) =
        scheduleRepository.existsById(id)

    override fun findAll(pageable: Pageable) =
        scheduleRepository.findAll(pageable).map { it.toResponse() }

}