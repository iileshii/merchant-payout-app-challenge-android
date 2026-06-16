package com.example.androidinterview.feature.transactions

import com.example.androidinterview.data.api.MerchantService
import com.example.androidinterview.data.api.model.Activities
import com.example.androidinterview.data.api.model.ActivityItem
import com.example.androidinterview.data.api.model.MerchantResponse
import com.example.androidinterview.data.api.model.PayoutRequest
import com.example.androidinterview.data.api.model.PayoutResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionListViewModelTest {

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
    fun `viewModel exposes transactions flow`() = runTest {
        val viewModel = TransactionListViewModel(FakeMerchantService())
        assertNotNull(viewModel.transactions)
    }
}

private class FakeMerchantService : MerchantService {
    override suspend fun getMerchant(): MerchantResponse {
        return MerchantResponse(0, 0, "GBP", emptyList())
    }

    override suspend fun getActivity(cursor: String?, limit: Int): Activities {
        return Activities(
            items = listOf(
                ActivityItem("1", "deposit", 1000, "GBP", "2026-06-16T09:51:11Z", "desc", "status")
            ),
            nextCursor = null,
            hasMore = false
        )
    }

    override suspend fun createPayout(request: PayoutRequest): PayoutResponse {
        throw NotImplementedError()
    }

    override suspend fun getPayout(id: String): PayoutResponse {
        throw NotImplementedError()
    }
}
