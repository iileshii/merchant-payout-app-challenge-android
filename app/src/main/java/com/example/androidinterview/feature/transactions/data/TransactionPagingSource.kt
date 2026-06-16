package com.example.androidinterview.feature.transactions.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidinterview.data.api.MerchantService
import com.example.androidinterview.feature.home.data.BalanceActivity
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.feature.home.data.HomeMapper

class TransactionPagingSource(
    private val merchantService: MerchantService,
    private val currency: BalanceCurrency
) : PagingSource<String, BalanceActivity>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, BalanceActivity> {
        return try {
            val cursor = params.key
            val response = merchantService.getActivity(cursor = cursor, limit = params.loadSize)

            val items = HomeMapper.mapActivities(response, currency)

            LoadResult.Page(
                data = items,
                prevKey = null, // Only forward paging supported
                nextKey = if (response.hasMore) response.nextCursor else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, BalanceActivity>): String? {
        return null // Always start from beginning on refresh
    }
}
