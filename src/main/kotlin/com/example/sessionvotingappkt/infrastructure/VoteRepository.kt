package com.example.sessionvotingappkt.infrastructure

import com.example.sessionvotingappkt.infrastructure.data.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SessionReport {
    fun getOption(): String
    fun getCount(): Int
}

interface ScheduleReport {
    fun getId(): Long
    fun getOption(): String
    fun getCount(): Int
}

interface VoteRepository : JpaRepository<Vote, Long> {

    fun existsByIdSessionAndIdAssociate(sessionId: Long, associateId: Long): Boolean

    @Query(
        "SELECT s.id as id, v.option as option, count(v.id) as count FROM Vote v " +
                "JOIN v.session s " +
                "WHERE s.idSchedule = :scheduleId GROUP BY id, option"
    )
    fun findVoteReportOfSchedule(scheduleId: Long): List<ScheduleReport>

    @Query(
        "SELECT v.option as option, count(v.id) as count FROM Vote v " +
                "WHERE v.idSession = :sessionId GROUP BY v.option"
    )
    fun findVoteReportOfSession(sessionId: Long): List<SessionReport>

}