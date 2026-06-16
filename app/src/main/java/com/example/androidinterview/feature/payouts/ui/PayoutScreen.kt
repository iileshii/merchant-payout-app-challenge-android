package com.example.androidinterview.feature.payouts.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidinterview.feature.payouts.PayoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutScreen(viewModel: PayoutViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payout", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                state.successAmount != null && state.successCurrency != null -> {
                    PayoutSuccessScreen(
                        amount = state.successAmount!!,
                        currency = state.successCurrency!!,
                        onAction = { viewModel.reset() }
                    )
                }

                state.error != null -> {
                    PayoutErrorScreen(
                        error = state.error!!,
                        onAction = { viewModel.onAmountChange(state.amount) /* Clears error */ }
                    )
                }

                else -> {
                    PayoutForm(
                        state = state,
                        onAmountChange = { viewModel.onAmountChange(it) },
                        onCurrencyChange = { viewModel.onCurrencyChange(it) },
                        onIbanChange = { viewModel.onIbanChange(it) },
                        onConfirmClick = { viewModel.showConfirmation() }
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (state.isConfirming) {
                PayoutConfirmationDialog(
                    amount = state.amount,
                    currency = state.currency,
                    iban = state.iban,
                    onDismiss = { viewModel.dismissConfirmation() },
                    onConfirm = { viewModel.initiatePayout() }
                )
            }
        }
    }
}
