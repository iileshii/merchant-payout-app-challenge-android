package com.example.androidinterview.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidinterview.feature.home.data.ActivityType
import com.example.androidinterview.feature.home.data.BalanceActivity
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.ui.theme.AndroidInterviewTheme
import com.example.androidinterview.ui.theme.NegativeRed
import com.example.androidinterview.ui.theme.PositiveGreen

@Composable
fun ActivityRow(activity: BalanceActivity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = activity.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatCurrency(activity.amount, activity.currency),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (activity.isIncome()) PositiveGreen else NegativeRed
        )
    }
}

private fun BalanceActivity.isIncome() =
    activityType == ActivityType.DEPOSIT || activityType == ActivityType.REFUND

@Preview(showBackground = true)
@Composable
fun ActivityRowPreview() {
    AndroidInterviewTheme {
        ActivityRow(
            activity = BalanceActivity(
                id = "asd1",
                description = "Payment",
                amount = 1000,
                currency = BalanceCurrency.EUR,
                activityType = ActivityType.PAYOUT,
            )
        )
    }
}
