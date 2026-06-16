package com.example.androidinterview.feature.payouts.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.feature.payouts.PayoutFormState
import com.example.androidinterview.ui.theme.ButtonText
import com.example.androidinterview.ui.theme.LabelGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutForm(
    state: PayoutFormState,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (BalanceCurrency) -> Unit,
    onIbanChange: (String) -> Unit,
    onConfirmClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Amount", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = onAmountChange,
                    placeholder = { Text("0.00", color = LabelGrey) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonText,
                        unfocusedBorderColor = LabelGrey.copy(alpha = 0.3f)
                    )
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Currency", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                LabelGrey.copy(alpha = 0.3f)
                            )
                        )
                    ) {
                        Text(text = state.currency.code)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        BalanceCurrency.entries.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text(currency.code) },
                                onClick = {
                                    onCurrencyChange(currency)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "IBAN", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.iban,
            onValueChange = onIbanChange,
            placeholder = { Text("GB29NWBK60161331926819", color = LabelGrey) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ButtonText,
                unfocusedBorderColor = LabelGrey.copy(alpha = 0.3f)
            )
        )
        Text(
            text = "Enter the destination bank account IBAN",
            style = MaterialTheme.typography.bodySmall,
            color = LabelGrey,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onConfirmClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = state.isValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isValid) ButtonText else LabelGrey.copy(alpha = 0.1f),
                contentColor = if (state.isValid) Color.White else LabelGrey.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Confirm", fontWeight = FontWeight.Bold)
        }
    }
}
