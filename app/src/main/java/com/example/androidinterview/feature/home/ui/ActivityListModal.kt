package com.example.androidinterview.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.androidinterview.feature.home.ActivityListState
import com.example.androidinterview.ui.theme.DividerGrey

import androidx.compose.ui.tooling.preview.Preview
import com.example.androidinterview.feature.home.data.ActivityType
import com.example.androidinterview.feature.home.data.BalanceActivity
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.ui.theme.AndroidInterviewTheme

@Composable
fun ActivityListModal(
    state: ActivityListState,
    onLoadMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp) // Fixed height for simplicity in modal
            .padding(16.dp)
    ) {
        Text(
            text = "All Activity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.items) { activity ->
                ActivityRow(activity)
                HorizontalDivider(color = DividerGrey)
            }
            item {
                if (state.hasMore) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Button(onClick = onLoadMore) {
                                Text("Load More")
                            }
                        }
                    }
                }
            }
            if (state.error != null) {
                item {
                    Text(
                        text = "Error: ${state.error}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityListModalPreview() {
    AndroidInterviewTheme {
        ActivityListModal(
            state = ActivityListState(
                items = listOf(
                    BalanceActivity(
                        id = "1",
                        description = "Payment from Customer",
                        amount = 12345,
                        currency = BalanceCurrency.GBP,
                        activityType = ActivityType.DEPOSIT
                    ),
                    BalanceActivity(
                        id = "2",
                        description = "Payout to Supplier",
                        amount = 125000,
                        currency = BalanceCurrency.EUR,
                        activityType = ActivityType.PAYOUT
                    ),
                    BalanceActivity(
                        id = "3",
                        description = "Refund to Client",
                        amount = 1500,
                        currency = BalanceCurrency.GBP,
                        activityType = ActivityType.REFUND
                    )
                ),
                hasMore = true
            ),
            onLoadMore = {}
        )
    }
}
