package com.example.androidinterview.feature.payouts.ui

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidinterview.feature.payouts.PayoutViewModel
import com.example.androidinterview.util.AndroidBiometricAuthenticator
import com.example.androidinterview.util.AndroidDeviceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutScreen() {
    val context = LocalContext.current
    var showScreenshotWarning by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val activity = context as? Activity
        val callback = Activity.ScreenCaptureCallback {
            showScreenshotWarning = true
        }

        activity?.registerScreenCaptureCallback(context.mainExecutor, callback)

        onDispose {
            activity?.unregisterScreenCaptureCallback(callback)
        }
    }

    val viewModel: PayoutViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val authenticator = AndroidBiometricAuthenticator(context as FragmentActivity)
                return PayoutViewModel(AndroidDeviceManager(context), authenticator) as T
            }
        }
    )
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

            if (showScreenshotWarning) {
                AlertDialog(
                    onDismissRequest = { showScreenshotWarning = false },
                    title = { Text(text = "Security Reminder", fontWeight = FontWeight.Bold) },
                    text = {
                        Text(text = "Please keep your financial data private. Screenshots may contain sensitive information.")
                    },
                    confirmButton = {
                        TextButton(onClick = { showScreenshotWarning = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
