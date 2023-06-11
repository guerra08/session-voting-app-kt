package com.example.sessionvotingappkt.service

import com.example.sessionvotingappkt.infrastructure.ScheduleRepository
import com.example.sessionvotingappkt.infrastructure.data.Schedule
import com.example.sessionvotingappkt.service.impl.DefaultScheduleService
import com.example.sessionvotingappkt.web.dto.request.CreateScheduleRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

class ScheduleServiceTests {

    private val scheduleRepository: ScheduleRepository = mock()
    private val sut: ScheduleService = DefaultScheduleService(scheduleRepository)

    @Test
    fun `should create a new schedule`() {
        val dto = CreateScheduleRequest(name = "Aumento das taxas")

        whenever(scheduleRepository.save(any())).thenReturn(Schedule(id = 1L, name = dto.name))

        val result = sut.create(dto)

        assertEquals(1L, result.id)
        assertEquals(dto.name, result.name)

        verify(scheduleRepository, times(1)).save(any())
    }

    @Test
    fun `should return null when finding by non existing id`() {
        whenever(scheduleRepository.findById(1L)).thenReturn(Optional.empty())

        val result = sut.findById(1L)

        assertNull(result)

        verify(scheduleRepository, times(1)).findById(1L)
    }

    @Test
    fun `should return schedule when finding by existing id`() {
        val entity = Schedule(id = 1L, name = "Aumento das taxas")
        whenever(scheduleRepository.findById(1L)).thenReturn(Optional.of(entity))

        val result = sut.findById(1L)

        assertNotNull(result)

        verify(scheduleRepository, times(1)).findById(1L)
    }

    @Test
    fun `should return that schedule exists`() {
        whenever(scheduleRepository.existsById(1L)).thenReturn(true)

        val result = sut.existsById(1L)

        assertTrue(result)
    }

    @Test
    fun `should return that schedule does not exists`() {
        whenever(scheduleRepository.existsById(1L)).thenReturn(false)

        val result = sut.existsById(1L)

        assertFalse(result)
    }

    @Test
    fun `should return empty response response of all schedules`() {
        whenever(scheduleRepository.findAll(any(Pageable::class.java))).thenReturn(Page.empty())

        val result = sut.findAll(Pageable.ofSize(10))

        assertNotNull(result)
        assertEquals(0, result.totalElements)
    }

    @Test
    fun `should return paginated response of all schedules`() {
        val page = PageImpl(mutableListOf(Schedule(1L, "Test")))
        whenever(scheduleRepository.findAll(any(Pageable::class.java))).thenReturn(page)

        val result = sut.findAll(Pageable.ofSize(10))

        assertNotNull(result)
        assertEquals(1, result.totalElements)
    }

}