package com.example.androidinterview.feature.home.data

import java.time.Instant

enum class BalanceCurrency(val code: String) {
    GBP("GBP"),
    EUR("EUR"),
}

enum class ActivityType(val value: String, val displayName: String) {
    PAYOUT("payout", "Payout"),
    DEPOSIT("deposit", "Deposit"),
    REFUND("refund", "Refund"),
    FEE("fee", "Fee"),
}

data class BalanceActivity(
    val id: String,
    val description: String,
    val amount: Long,
    val currency: BalanceCurrency,
    val activityType: ActivityType,
    val date: Instant,
    val status: String,
)
