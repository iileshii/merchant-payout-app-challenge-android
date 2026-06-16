package com.example.androidinterview.feature.home.data

data class MerchantBalance(
    val available: Long,
    val pending: Long,
    val currency: BalanceCurrency,
    val recentActivity: List<BalanceActivity>
)
