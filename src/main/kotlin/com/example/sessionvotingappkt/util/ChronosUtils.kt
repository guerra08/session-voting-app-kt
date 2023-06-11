package com.example.sessionvotingappkt.util

import java.time.Instant

infix fun Instant.isAfter(secondLocalDateTime: Instant) =
    this.isAfter(secondLocalDateTime)

infix fun Instant.isBefore(secondLocalDateTime: Instant) =
    this.isBefore(secondLocalDateTime)