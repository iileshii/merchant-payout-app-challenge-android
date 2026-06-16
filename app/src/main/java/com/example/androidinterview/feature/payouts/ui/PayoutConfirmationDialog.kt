package com.example.androidinterview.feature.payouts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.feature.home.ui.formatCurrency
import com.example.androidinterview.ui.theme.ButtonText
import com.example.androidinterview.ui.theme.LabelGrey

@Composable
fun PayoutConfirmationDialog(
    amount: String,
    currency: BalanceCurrency,
    iban: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Confirm Payout",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                SummaryRow(
                    label = "Amount:",
                    value = formatCurrency(
                        ((amount.toDoubleOrNull() ?: 0.0) * 100).toLong(),
                        currency
                    )
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = LabelGrey.copy(alpha = 0.1f)
                )
                SummaryRow(label = "Currency:", value = currency.code)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = LabelGrey.copy(alpha = 0.1f)
                )
                SummaryRow(label = "IBAN:", value = maskIban(iban))

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LabelGrey.copy(alpha = 0.1f),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Cancel", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonText,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = LabelGrey)
        Text(text = value, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

private fun maskIban(iban: String): String {
    val clean = iban.replace(" ", "").uppercase()
    if (clean.length < 8) return clean
    val prefix = clean.take(4)
    val suffix = clean.takeLast(4)
    val masked = "*".repeat(clean.length - 8)
    return "$prefix$masked$suffix"
}
