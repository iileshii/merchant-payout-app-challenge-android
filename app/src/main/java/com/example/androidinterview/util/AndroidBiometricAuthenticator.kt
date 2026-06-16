package com.example.androidinterview.util

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidBiometricAuthenticator(
    private val activity: FragmentActivity
) : BiometricAuthenticator {

    override suspend fun authenticate(): BiometricResult =
        suspendCancellableCoroutine { continuation ->
            val biometricManager = BiometricManager.from(activity)
            val canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )

            if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
                continuation.resume(BiometricResult.NotEnrolled)
                return@suspendCancellableCoroutine
            }

            val executor = ContextCompat.getMainExecutor(activity)
            val biometricPrompt = BiometricPrompt(
                activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        val result = if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                        ) {
                            BiometricResult.Cancelled
                        } else {
                            BiometricResult.Error(errString.toString())
                        }
                        if (continuation.isActive) continuation.resume(result)
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        if (continuation.isActive) continuation.resume(BiometricResult.Success)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // Prompt remains open on single failed attempts
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate to complete the payout")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

            biometricPrompt.authenticate(promptInfo)

            continuation.invokeOnCancellation {
                biometricPrompt.cancelAuthentication()
            }
        }
}
