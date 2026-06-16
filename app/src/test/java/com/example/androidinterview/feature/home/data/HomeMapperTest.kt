package com.example.androidinterview.feature.home.data

import com.example.androidinterview.data.api.model.ActivityItem
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class HomeMapperTest {

    @Test
    fun `mapActivityItem correctly parses date and status`() {
        val dateString = "2026-06-16T09:51:11Z"
        val item = ActivityItem(
            id = "1",
            type = "deposit",
            amount = 1000,
            currency = "GBP",
            date = dateString,
            description = "Test",
            status = "completed"
        )

        val result = HomeMapper.mapActivities(
            com.example.androidinterview.data.api.model.Activities(listOf(item), null, false),
            BalanceCurrency.GBP
        ).first()

        assertEquals(Instant.parse(dateString), result.date)
        assertEquals("completed", result.status)
        assertEquals(ActivityType.DEPOSIT, result.activityType)
    }
}
