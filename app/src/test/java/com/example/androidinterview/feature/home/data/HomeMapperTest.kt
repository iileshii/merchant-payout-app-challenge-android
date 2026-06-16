package com.example.androidinterview.feature.home.data

import com.example.androidinterview.data.api.model.Activities
import com.example.androidinterview.data.api.model.ActivityItem
import com.example.androidinterview.data.api.model.MerchantResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeMapperTest {

    @Test
    fun `mapMerchantResponse maps correctly`() {
        val response = MerchantResponse(
            availableBalance = 500000,
            pendingBalance = 25000,
            currency = "GBP",
            activity = listOf(
                ActivityItem("1", "deposit", 1000, "GBP", "date", "desc", "status")
            )
        )

        val result = HomeMapper.mapMerchantResponse(response)

        assertEquals(500000L, result.available)
        assertEquals(25000L, result.pending)
        assertEquals(BalanceCurrency.GBP, result.currency)
        assertEquals(1, result.recentActivity.size)
        assertEquals("1", result.recentActivity[0].id)
        assertEquals(ActivityType.DEPOSIT, result.recentActivity[0].activityType)
    }

    @Test
    fun `mapMerchantResponse fallbacks on unknown currency`() {
        val response = MerchantResponse(
            availableBalance = 0,
            pendingBalance = 0,
            currency = "UNKNOWN",
            activity = emptyList()
        )

        val result = HomeMapper.mapMerchantResponse(response)

        assertEquals(BalanceCurrency.GBP, result.currency)
    }

    @Test
    fun `mapActivities maps correctly`() {
        val response = Activities(
            items = listOf(
                ActivityItem("1", "payout", 2000, "EUR", "date", "payout desc", "status")
            ),
            nextCursor = "cursor",
            hasMore = true
        )

        val result = HomeMapper.mapActivities(response, BalanceCurrency.EUR)

        assertEquals(1, result.size)
        assertEquals("payout desc", result[0].description)
        assertEquals(ActivityType.PAYOUT, result[0].activityType)
        assertEquals(BalanceCurrency.EUR, result[0].currency)
    }
}
