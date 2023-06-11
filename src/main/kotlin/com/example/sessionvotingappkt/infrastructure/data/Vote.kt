package com.example.sessionvotingappkt.infrastructure.data

import com.example.sessionvotingappkt.util.VotingOptions
import com.example.sessionvotingappkt.web.dto.response.VoteResponse
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "votes", uniqueConstraints = [UniqueConstraint(columnNames = ["id_associate", "id_session"])])
class Vote(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    var id: Long? = null,
    @Column(name = "id_associate")
    val idAssociate: Long,
    @Column(name = "option")
    val option: String,
    @Column(name = "id_session")
    val idSession: Long,
    @Column(name = "creation_time")
    val creationTime: Instant,
    @ManyToOne
    @JoinColumn(name = "id_session", insertable = false, updatable = false)
    val session: Session? = null
)

fun Vote.toResponse() = VoteResponse(this.id, VotingOptions.valueOf(this.option), this.creationTime)
