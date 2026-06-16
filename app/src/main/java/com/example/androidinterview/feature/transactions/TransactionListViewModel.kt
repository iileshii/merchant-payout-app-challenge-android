package com.example.androidinterview.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.androidinterview.data.api.MerchantService
import com.example.androidinterview.data.api.NetworkModule
import com.example.androidinterview.feature.home.data.BalanceActivity
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.feature.transactions.data.TransactionPagingSource
import com.example.androidinterview.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class TransactionListItem {
    data class Header(val title: String) : TransactionListItem()
    data class Transaction(val activity: BalanceActivity) : TransactionListItem()
}

class TransactionListViewModel(
    private val merchantService: MerchantService = NetworkModule.merchantService
) : ViewModel() {

    val transactions: Flow<PagingData<TransactionListItem>> = Pager(
        config = PagingConfig(pageSize = 15, enablePlaceholders = false),
        pagingSourceFactory = { TransactionPagingSource(merchantService, BalanceCurrency.GBP) }
    ).flow
        .map { pagingData ->
            pagingData.map { TransactionListItem.Transaction(it) }
                .insertSeparators { before, after ->
                    val beforeDate = before?.activity?.date?.let { DateUtils.formatHeaderDate(it) }
                    val afterDate = after?.activity?.date?.let { DateUtils.formatHeaderDate(it) }

                    if (afterDate != null && beforeDate != afterDate) {
                        TransactionListItem.Header(afterDate)
                    } else {
                        null
                    }
                }
        }
        .cachedIn(viewModelScope)
}
