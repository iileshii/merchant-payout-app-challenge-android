package com.example.androidinterview.feature.home.data

import com.example.androidinterview.data.api.model.Activities
import com.example.androidinterview.data.api.model.ActivityItem
import com.example.androidinterview.data.api.model.MerchantResponse
import java.time.Instant

object HomeMapper {

    fun mapMerchantResponse(response: MerchantResponse): MerchantBalance {
        val currency = mapCurrency(response.currency)
        return MerchantBalance(
            available = response.availableBalance.toLong(),
            pending = response.pendingBalance.toLong(),
            currency = currency,
            recentActivity = response.activity.map { mapActivityItem(it, currency) }
        )
    }

    fun mapActivities(response: Activities, currency: BalanceCurrency): List<BalanceActivity> {
        return response.items.map { mapActivityItem(it, currency) }
    }

    private fun mapActivityItem(item: ActivityItem, currency: BalanceCurrency): BalanceActivity {
        return BalanceActivity(
            id = item.id,
            description = item.description,
            amount = item.amount.toLong(),
            currency = currency,
            activityType = mapActivityType(item.type),
            date = Instant.parse(item.date),
            status = item.status
        )
    }

    private fun mapCurrency(code: String): BalanceCurrency {
        return BalanceCurrency.entries.find { it.code == code } ?: BalanceCurrency.GBP
    }

    private fun mapActivityType(type: String): ActivityType {
        return ActivityType.entries.find { it.value == type } ?: ActivityType.DEPOSIT
    }
}
