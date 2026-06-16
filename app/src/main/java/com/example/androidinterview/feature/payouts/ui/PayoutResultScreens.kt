package com.example.androidinterview.feature.payouts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.feature.home.ui.formatCurrency
import com.example.androidinterview.ui.theme.ButtonText
import com.example.androidinterview.ui.theme.NegativeRed
import com.example.androidinterview.ui.theme.PositiveGreen

@Composable
fun PayoutSuccessScreen(
    amount: String,
    currency: BalanceCurrency,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = PositiveGreen
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.padding(16.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Payout Completed",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        val formattedAmount =
            formatCurrency(((amount.toDoubleOrNull() ?: 0.0) * 100).toLong(), currency)
        Text(
            text = "Your payout of $formattedAmount has been processed successfully.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onAction,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonText),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Create Another Payout", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PayoutErrorScreen(
    error: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = NegativeRed
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Unable to Process Payout",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = NegativeRed,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = NegativeRed.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onAction,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonText),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Try Again", fontWeight = FontWeight.Bold)
        }
    }
}
