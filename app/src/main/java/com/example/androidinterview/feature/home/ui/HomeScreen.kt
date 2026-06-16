package com.example.androidinterview.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidinterview.feature.home.HomeUiState
import com.example.androidinterview.feature.home.HomeViewModel
import com.example.androidinterview.feature.home.data.ActivityType
import com.example.androidinterview.feature.home.data.BalanceActivity
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.ui.theme.AndroidInterviewTheme
import com.example.androidinterview.ui.theme.ButtonBackground
import com.example.androidinterview.ui.theme.ButtonText
import com.example.androidinterview.ui.theme.DividerGrey
import com.example.androidinterview.ui.theme.LabelGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val activityListState by viewModel.activityListState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Business Account",
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${state.message}", color = Color.Red)
                        Button(onClick = { viewModel.fetchMerchantData() }) {
                            Text("Retry")
                        }
                    }
                }
                is HomeUiState.Success -> {
                    HomeContent(
                        state = state,
                        onShowMoreClick = { showSheet = true }
                    )
                }
            }
        }

        if (showSheet) {
            LaunchedEffect(showSheet) {
                if (activityListState.items.isEmpty()) {
                    viewModel.fetchMoreActivity()
                }
            }
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                ActivityListModal(
                    state = activityListState,
                    onLoadMore = { viewModel.fetchMoreActivity() }
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    state: HomeUiState.Success,
    onShowMoreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Account Balance",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            BalanceItem(
                label = "Available",
                amount = state.availableBalance,
                currency = state.currency,
                modifier = Modifier.weight(1f)
            )
            BalanceItem(
                label = "Pending",
                amount = state.pendingBalance,
                currency = state.currency,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        state.recentActivity.take(3).forEach { activity ->
            ActivityRow(activity)
            HorizontalDivider(color = DividerGrey)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onShowMoreClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonBackground,
                contentColor = ButtonText
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Show More", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BalanceItem(
    label: String,
    amount: Long,
    currency: BalanceCurrency,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, color = LabelGrey, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = formatCurrency(amount, currency),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    AndroidInterviewTheme {
        HomeContent(
            state = HomeUiState.Success(
                availableBalance = 500000,
                pendingBalance = 25000,
                currency = BalanceCurrency.GBP,
                recentActivity = listOf(
                    BalanceActivity(
                        id = "1",
                        description = "Payment from Customer Qwe",
                        amount = 12345,
                        currency = BalanceCurrency.EUR,
                        activityType = ActivityType.DEPOSIT
                    ),
                    BalanceActivity(
                        id = "2",
                        description = "Payment from Customer Asd",
                        amount = 987654,
                        currency = BalanceCurrency.GBP,
                        activityType = ActivityType.DEPOSIT
                    ),
                    BalanceActivity(
                        id = "3",
                        description = "Payment from Customer Zxc",
                        amount = 150000,
                        currency = BalanceCurrency.GBP,
                        activityType = ActivityType.DEPOSIT
                    )
                )
            ),
            onShowMoreClick = {}
        )
    }
}
