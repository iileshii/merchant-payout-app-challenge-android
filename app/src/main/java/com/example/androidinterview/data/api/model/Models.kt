package com.example.androidinterview.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MerchantResponse(
    @SerialName("available_balance") val availableBalance: Int,
    @SerialName("pending_balance") val pendingBalance: Int,
    @SerialName("currency") val currency: String,
    @SerialName("activity") val activity: List<ActivityItem>
)

@Serializable
data class ActivityItem(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("amount") val amount: Int,
    @SerialName("currency") val currency: String,
    @SerialName("date") val date: String,
    @SerialName("description") val description: String,
    @SerialName("status") val status: String
)

@Serializable
data class Activities(
    @SerialName("items") val items: List<ActivityItem>,
    @SerialName("next_cursor") val nextCursor: String?,
    @SerialName("has_more") val hasMore: Boolean
)

@Serializable
data class PayoutRequest(
    @SerialName("amount") val amount: Int,
    @SerialName("currency") val currency: String,
    @SerialName("iban") val iban: String,
    @SerialName("device_id") val deviceId: String
)

@Serializable
data class PayoutResponse(
    @SerialName("id") val id: String,
    @SerialName("status") val status: String,
    @SerialName("amount") val amount: Int,
    @SerialName("currency") val currency: String,
    @SerialName("iban") val iban: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class ErrorResponse(
    @SerialName("error") val error: String,
    @SerialName("code") val code: String? = null
)
