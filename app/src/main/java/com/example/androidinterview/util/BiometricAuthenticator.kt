package com.example.androidinterview.util

sealed class BiometricResult {
    data object Success : BiometricResult()
    data object Cancelled : BiometricResult()
    data object NotEnrolled : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}

interface BiometricAuthenticator {
    suspend fun authenticate(): BiometricResult
}
