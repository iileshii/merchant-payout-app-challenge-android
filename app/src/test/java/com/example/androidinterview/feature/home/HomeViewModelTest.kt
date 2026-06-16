package com.example.androidinterview.feature.home

import com.example.androidinterview.data.api.MerchantService
import com.example.androidinterview.data.api.model.Activities
import com.example.androidinterview.data.api.model.ActivityItem
import com.example.androidinterview.data.api.model.MerchantResponse
import com.example.androidinterview.feature.home.data.BalanceCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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
    fun `initial state is Success if fetch finishes immediately`() = runTest {
        // With UnconfinedTestDispatcher, the init block coroutine might finish immediately
        val viewModel = HomeViewModel(FakeMerchantService(), testDispatcher)
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
    }

    @Test
    fun `fetchMerchantData success updates uiState to Success`() = runTest {
        val fakeService = FakeMerchantService()
        val viewModel = HomeViewModel(fakeService, testDispatcher)

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        val successState = state as HomeUiState.Success
        assertEquals(500000L, successState.availableBalance)
        assertEquals(BalanceCurrency.GBP, successState.currency)
    }

    @Test
    fun `fetchMerchantData error updates uiState to Error`() = runTest {
        val fakeService = FakeMerchantService(shouldFail = true)
        val viewModel = HomeViewModel(fakeService, testDispatcher)

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertEquals("Network error", (state as HomeUiState.Error).message)
    }

    @Test
    fun `fetchMoreActivity updates activityListState`() = runTest {
        val fakeService = FakeMerchantService()
        val viewModel = HomeViewModel(fakeService, testDispatcher)

        viewModel.fetchMoreActivity()

        val listState = viewModel.activityListState.value
        assertEquals(1, listState.items.size)
        assertEquals("2", listState.items[0].id)
        assertEquals("next_cursor", listState.nextCursor)
    }
}

class FakeMerchantService(private val shouldFail: Boolean = false) : MerchantService {
    override suspend fun getMerchant(): MerchantResponse {
        if (shouldFail) throw Exception("Network error")
        return MerchantResponse(
            availableBalance = 500000,
            pendingBalance = 25000,
            currency = "GBP",
            activity = listOf(
                ActivityItem("1", "deposit", 1000, "GBP", "date", "desc", "status")
            )
        )
    }

    override suspend fun getActivity(cursor: String?, limit: Int): Activities {
        if (shouldFail) throw Exception("Network error")
        return Activities(
            items = listOf(
                ActivityItem("2", "payout", 2000, "GBP", "date", "payout", "status")
            ),
            nextCursor = "next_cursor",
            hasMore = true
        )
    }
}
