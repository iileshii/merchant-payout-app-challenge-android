package com.example.androidinterview.feature.home.ui

import com.example.androidinterview.feature.home.data.BalanceCurrency
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class FormatHelperTest {

    @Test
    fun `formatCurrency formats GBP correctly`() {
        Locale.setDefault(Locale.UK)
        val amount = 500000L
        val currency = BalanceCurrency.GBP

        val result = formatCurrency(amount, currency)

        assertEquals("£5,000.00", result)
    }

    @Test
    fun `formatCurrency formats EUR correctly`() {
        Locale.setDefault(Locale.UK)
        val amount = 12345L
        val currency = BalanceCurrency.EUR

        val result = formatCurrency(amount, currency)

        assertEquals("€123.45", result)
    }

    @Test
    fun `formatCurrency handles zero amount`() {
        Locale.setDefault(Locale.UK)
        val result = formatCurrency(0L, BalanceCurrency.GBP)
        assertEquals("£0.00", result)
    }
}
