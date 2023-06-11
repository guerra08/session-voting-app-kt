package com.example.sessionvotingappkt.service

import com.example.sessionvotingappkt.web.dto.request.CreateScheduleRequest
import com.example.sessionvotingappkt.web.dto.response.ScheduleResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ScheduleService {
    /**
     * Creates a new Schedule entry in the database
     * @param createScheduleDto The DTO to create a new 'Schedule'
     * @return Mono with the newly created 'Schedule'
     */
    fun create(createScheduleDto: CreateScheduleRequest): ScheduleResponse

    /**
     * Tries to find a 'Schedule' by its id
     * @param id The id
     * @return Nullable of 'Schedule'
     */
    fun findById(id: Long): ScheduleResponse?

    /**
     * Checks if a 'Schedule' exists by its id
     * @param id The id of the Schedule
     * @return boolean
     */
    fun existsById(id: Long): Boolean

    /**
     * Finds all Schedules with pagination
     */
    fun findAll(pageable: Pageable): Page<ScheduleResponse>
}