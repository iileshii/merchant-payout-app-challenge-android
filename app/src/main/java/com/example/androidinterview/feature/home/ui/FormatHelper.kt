package com.example.androidinterview.feature.home.ui

import com.example.androidinterview.feature.home.data.BalanceCurrency
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency

fun formatCurrency(amount: Long, currency: BalanceCurrency): String {

    val currencyInstance = Currency.getInstance(currency.code)
    val fractionDigits = currencyInstance.defaultFractionDigits
    val divisor = BigDecimal.TEN.pow(fractionDigits)

    val amount = BigDecimal(amount).divide(divisor, fractionDigits, RoundingMode.UNNECESSARY)

    val formatter = NumberFormat.getCurrencyInstance().apply {
        this.currency = currencyInstance
        maximumFractionDigits = fractionDigits
        minimumFractionDigits = fractionDigits
    }

    return formatter.format(amount)
}
