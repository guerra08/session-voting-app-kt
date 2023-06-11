package com.example.sessionvotingappkt.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class ChronosUtilsTests {

    @Test
    fun `current local date time should be later than previous local date time`() {

        val instant = Instant.now()
        val previous = instant.minusSeconds(10L)

        val result = instant isAfter previous

        assertTrue(result)
    }

    @Test
    fun `previous instant should be later than current instant`() {

        val instant = Instant.now()
        val previous = instant.minusSeconds(10L)

        val result = previous isBefore instant

        assertTrue(result)
    }

    @Test
    fun `previous instant is equal to current instant`() {

        val instant = Instant.now()

        val result = instant isBefore instant

        assertFalse(result)
    }

}
