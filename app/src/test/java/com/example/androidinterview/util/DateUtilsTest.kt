package com.example.androidinterview.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class DateUtilsTest {

    @Test
    fun `formatHeaderDate returns Today for current date`() {
        val now = Instant.now()
        assertEquals("Today", DateUtils.formatHeaderDate(now))
    }

    @Test
    fun `formatHeaderDate returns Yesterday for previous day`() {
        val yesterday = Instant.now().minusSeconds(24 * 60 * 60)
        assertEquals("Yesterday", DateUtils.formatHeaderDate(yesterday))
    }

    @Test
    fun `formatHeaderDate returns formatted date for older dates`() {
        // 2025-05-18T10:00:00Z
        val oldDate = Instant.parse("2025-05-18T10:00:00Z")
        // Note: result might depend on system timezone, but for 18 May 2025 it should be consistent
        val expected = "18 May 2025"
        assertEquals(expected, DateUtils.formatHeaderDate(oldDate))
    }

    @Test
    fun `formatItemDate returns expected format`() {
        val date = Instant.parse("2026-01-16T10:00:00Z")
        val expected = "16 Jan 2026"
        assertEquals(expected, DateUtils.formatItemDate(date))
    }
}
