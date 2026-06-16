package com.example.androidinterview.feature.home.data

enum class BalanceCurrency(val code: String) {
    GBP("GBP"),
    EUR("EUR"),
}

enum class ActivityType(val value: String) {
    PAYOUT("payout"),
    DEPOSIT("deposit"),
    REFUND("refund"),
    FEE("fee"),
}

data class BalanceActivity(
    val id: String,
    val description: String,
    val amount: Long,
    val currency: BalanceCurrency,
    val activityType: ActivityType,
)
