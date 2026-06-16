package com.example.androidinterview.feature.transactions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.androidinterview.feature.home.data.ActivityType
import com.example.androidinterview.feature.home.data.BalanceActivity
import com.example.androidinterview.feature.home.ui.formatCurrency
import com.example.androidinterview.feature.transactions.TransactionListItem
import com.example.androidinterview.feature.transactions.TransactionListViewModel
import com.example.androidinterview.ui.theme.DividerGrey
import com.example.androidinterview.ui.theme.LabelGrey
import com.example.androidinterview.ui.theme.NegativeRed
import com.example.androidinterview.ui.theme.PositiveGreen
import com.example.androidinterview.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(viewModel: TransactionListViewModel = viewModel()) {
    val items = viewModel.transactions.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Activity", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(
                count = items.itemCount,
                key = items.itemKey {
                    when (it) {
                        is TransactionListItem.Header -> "header_${it.title}"
                        is TransactionListItem.Transaction -> it.activity.id
                    }
                }
            ) { index ->
                when (val item = items[index]) {
                    is TransactionListItem.Header -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item.title,
                                color = LabelGrey,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    is TransactionListItem.Transaction -> {
                        TransactionRow(item.activity)
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = DividerGrey
                        )
                    }

                    null -> {
                        // Placeholder for loading
                    }
                }
            }

            when (val state = items.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }

                is LoadState.Error -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Error loading more items", color = Color.Red)
                            Button(onClick = { items.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                else -> {}
            }

            if (items.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRow(activity: BalanceActivity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.activityType.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = DateUtils.formatItemDate(activity.date),
                style = MaterialTheme.typography.bodySmall,
                color = LabelGrey
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            val isIncome =
                activity.activityType == ActivityType.DEPOSIT || activity.activityType == ActivityType.REFUND
            Text(
                text = formatCurrency(activity.amount, activity.currency),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) PositiveGreen else NegativeRed
            )
            Text(
                text = activity.status.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = LabelGrey
            )
        }
    }
}
