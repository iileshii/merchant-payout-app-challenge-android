package com.example.androidinterview.feature.payouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidinterview.data.api.MerchantService
import com.example.androidinterview.data.api.NetworkModule
import com.example.androidinterview.data.api.model.ErrorResponse
import com.example.androidinterview.data.api.model.PayoutRequest
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.util.BiometricAuthenticator
import com.example.androidinterview.util.BiometricResult
import com.example.androidinterview.util.DeviceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException

data class PayoutFormState(
    val amount: String = "",
    val currency: BalanceCurrency = BalanceCurrency.GBP,
    val iban: String = "",
    val isConfirming: Boolean = false,
    val isLoading: Boolean = false,
    val successAmount: String? = null,
    val successCurrency: BalanceCurrency? = null,
    val error: String? = null
) {
    val isValid: Boolean
        get() {
            val amountValue = amount.toDoubleOrNull() ?: 0.0
            return amountValue > 0 && iban.isNotBlank() && isValidIban(iban)
        }

    private fun isValidIban(iban: String): Boolean {
        // Simple regex for IBAN validation (2 letters followed by digits/letters, typically 15-34 chars)
        val ibanRegex = "^[A-Z]{2}[0-9A-Z]{13,32}$".toRegex()
        return ibanRegex.matches(iban.replace(" ", "").uppercase())
    }
}

class PayoutViewModel(
    private val deviceManager: DeviceManager,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val merchantService: MerchantService = NetworkModule.merchantService
) : ViewModel() {

    private val _state = MutableStateFlow(PayoutFormState())
    val state: StateFlow<PayoutFormState> = _state.asStateFlow()

    fun onAmountChange(amount: String) {
        _state.update { it.copy(amount = amount, error = null) }
    }

    fun onCurrencyChange(currency: BalanceCurrency) {
        _state.update { it.copy(currency = currency, error = null) }
    }

    fun onIbanChange(iban: String) {
        _state.update { it.copy(iban = iban, error = null) }
    }

    fun showConfirmation() {
        if (_state.value.isValid) {
            _state.update { it.copy(isConfirming = true) }
        }
    }

    fun dismissConfirmation() {
        _state.update { it.copy(isConfirming = false) }
    }

    fun initiatePayout() {
        val currentState = _state.value
        if (!currentState.isValid) return

        _state.update { it.copy(isLoading = true, isConfirming = false, error = null) }

        viewModelScope.launch {
            try {
                // Convert decimal string to minor units (integer)
                val amountInt = (currentState.amount.toDouble() * 100).toInt()

                // Biometric check for payouts >= £1,000.00 (100,000 pence)
                if (amountInt >= 100000) {
                    when (val biometricResult = biometricAuthenticator.authenticate()) {
                        is BiometricResult.Success -> { /* Continue */
                        }

                        is BiometricResult.NotEnrolled -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Biometrics not set up. Please enable biometrics in device settings."
                                )
                            }
                            return@launch
                        }

                        is BiometricResult.Cancelled -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Biometric authentication was cancelled."
                                )
                            }
                            return@launch
                        }

                        is BiometricResult.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Biometric error: ${biometricResult.message}"
                                )
                            }
                            return@launch
                        }
                    }
                }

                merchantService.createPayout(
                    PayoutRequest(
                        amount = amountInt,
                        currency = currentState.currency.code,
                        iban = currentState.iban.replace(" ", "").uppercase(),
                        deviceId = deviceManager.getDeviceId()
                    )
                )

                _state.update {
                    it.copy(
                        isLoading = false,
                        successAmount = currentState.amount,
                        successCurrency = currentState.currency
                    )
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = try {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                    errorResponse.error
                } catch (ex: Exception) {
                    "An unexpected error occurred"
                }
                _state.update { it.copy(isLoading = false, error = message) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Network error. Please check your connection."
                    )
                }
            }
        }
    }

    fun reset() {
        _state.value = PayoutFormState()
    }
}
