package com.example.androidinterview.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val headerFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
    private val itemFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

    fun formatHeaderDate(instant: Instant): String {
        val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return when (date) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> date.format(headerFormatter)
        }
    }

    fun formatItemDate(instant: Instant): String {
        return instant.atZone(ZoneId.systemDefault()).toLocalDate().format(itemFormatter)
    }
}
