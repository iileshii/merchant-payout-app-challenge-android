package com.example.androidinterview.feature.payouts

import com.example.androidinterview.data.api.MerchantService
import com.example.androidinterview.data.api.model.Activities
import com.example.androidinterview.data.api.model.MerchantResponse
import com.example.androidinterview.data.api.model.PayoutRequest
import com.example.androidinterview.data.api.model.PayoutResponse
import com.example.androidinterview.util.DeviceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class PayoutViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty and invalid`() {
        val viewModel = PayoutViewModel(FakeDeviceManager())
        val state = viewModel.state.value
        assertEquals("", state.amount)
        assertEquals("", state.iban)
        assertFalse(state.isValid)
    }

    @Test
    fun `isValid is true when amount is positive and IBAN is valid`() {
        val viewModel = PayoutViewModel(FakeDeviceManager())
        viewModel.onAmountChange("100.00")
        viewModel.onIbanChange("GB29NWBK60161331926819")
        assertTrue(viewModel.state.value.isValid)
    }

    @Test
    fun `isValid is false when amount is zero or negative`() {
        val viewModel = PayoutViewModel(FakeDeviceManager())
        viewModel.onAmountChange("0")
        viewModel.onIbanChange("GB29NWBK60161331926819")
        assertFalse(viewModel.state.value.isValid)

        viewModel.onAmountChange("-10")
        assertFalse(viewModel.state.value.isValid)
    }

    @Test
    fun `isValid is false when IBAN is invalid`() {
        val viewModel = PayoutViewModel(FakeDeviceManager())
        viewModel.onAmountChange("100.00")
        viewModel.onIbanChange("INVALID_IBAN")
        assertFalse(viewModel.state.value.isValid)
    }

    @Test
    fun `initiatePayout success updates state with successAmount`() = runTest {
        val viewModel = PayoutViewModel(FakeDeviceManager(), FakeMerchantService())
        viewModel.onAmountChange("100.00")
        viewModel.onIbanChange("GB29NWBK60161331926819")

        viewModel.initiatePayout()

        val state = viewModel.state.value
        assertEquals("100.00", state.successAmount)
        assertNull(state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `initiatePayout sends amount in minor units`() = runTest {
        var sentAmount = 0
        val fakeService = object : FakeMerchantService() {
            override suspend fun createPayout(request: PayoutRequest): PayoutResponse {
                sentAmount = request.amount
                return super.createPayout(request)
            }
        }
        val viewModel = PayoutViewModel(FakeDeviceManager(), fakeService)
        viewModel.onAmountChange("888.88")
        viewModel.onIbanChange("GB29NWBK60161331926819")

        viewModel.initiatePayout()

        assertEquals(88888, sentAmount)
    }

    @Test
    fun `initiatePayout sends device id`() = runTest {
        var sentDeviceId = ""
        val fakeService = object : FakeMerchantService() {
            override suspend fun createPayout(request: PayoutRequest): PayoutResponse {
                sentDeviceId = request.deviceId
                return super.createPayout(request)
            }
        }
        val viewModel = PayoutViewModel(FakeDeviceManager("test_device"), fakeService)
        viewModel.onAmountChange("100.00")
        viewModel.onIbanChange("GB29NWBK60161331926819")

        viewModel.initiatePayout()

        assertEquals("test_device", sentDeviceId)
    }

    @Test
    fun `initiatePayout error updates state with error message`() = runTest {
        val viewModel = PayoutViewModel(FakeDeviceManager(), FakeMerchantService(shouldFail = true))
        viewModel.onAmountChange("100.00")
        viewModel.onIbanChange("GB29NWBK60161331926819")

        viewModel.initiatePayout()

        val state = viewModel.state.value
        assertNotNull(state.error)
        assertEquals("Network error. Please check your connection.", state.error)
        assertFalse(state.isLoading)
    }
}

private class FakeDeviceManager(private val id: String = "fake_id") : DeviceManager {
    override fun getDeviceId(): String = id
}

open class FakeMerchantService(private val shouldFail: Boolean = false) : MerchantService {
    override suspend fun getMerchant(): MerchantResponse {
        return MerchantResponse(0, 0, "GBP", emptyList())
    }

    override suspend fun getActivity(cursor: String?, limit: Int): Activities {
        return Activities(emptyList(), null, false)
    }

    override suspend fun createPayout(request: PayoutRequest): PayoutResponse {
        if (shouldFail) throw Exception("Network error")
        return PayoutResponse(
            id = "p_123",
            status = "pending",
            amount = request.amount,
            currency = request.currency,
            iban = request.iban,
            createdAt = Instant.now().toString()
        )
    }

    override suspend fun getPayout(id: String): PayoutResponse {
        return PayoutResponse("", "", 0, "", "", "")
    }
}
