package com.example.androidinterview.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidinterview.data.api.MerchantService
import com.example.androidinterview.data.api.NetworkModule
import com.example.androidinterview.feature.home.data.BalanceActivity
import com.example.androidinterview.feature.home.data.BalanceCurrency
import com.example.androidinterview.feature.home.data.HomeMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val availableBalance: Long,
        val pendingBalance: Long,
        val currency: BalanceCurrency,
        val recentActivity: List<BalanceActivity>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

data class ActivityListState(
    val items: List<BalanceActivity> = emptyList(),
    val isLoading: Boolean = false,
    val nextCursor: String? = null,
    val hasMore: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    merchantService: MerchantService? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val service: MerchantService by lazy {
        merchantService ?: NetworkModule.merchantService
    }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _activityListState = MutableStateFlow(ActivityListState())
    val activityListState: StateFlow<ActivityListState> = _activityListState.asStateFlow()

    private var fetchJob: Job? = null
    private var currentCurrency: BalanceCurrency = BalanceCurrency.GBP

    init {
        fetchMerchantData()
    }

    fun fetchMerchantData() {
        fetchJob?.cancel()
        _uiState.value = HomeUiState.Loading
        fetchJob = viewModelScope.launch {
            try {
                val response = withContext(ioDispatcher) {
                    service.getMerchant()
                }
                val merchantBalance = HomeMapper.mapMerchantResponse(response)
                currentCurrency = merchantBalance.currency
                _uiState.value = HomeUiState.Success(
                    availableBalance = merchantBalance.available,
                    pendingBalance = merchantBalance.pending,
                    currency = merchantBalance.currency,
                    recentActivity = merchantBalance.recentActivity
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchMoreActivity() {
        val currentState = _activityListState.value
        if (currentState.isLoading || !currentState.hasMore) return

        _activityListState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val response = withContext(ioDispatcher) {
                    service.getActivity(currentState.nextCursor)
                }
                val transformedItems = HomeMapper.mapActivities(response, currentCurrency)
                _activityListState.update {
                    it.copy(
                        items = it.items + transformedItems,
                        isLoading = false,
                        nextCursor = response.nextCursor,
                        hasMore = response.hasMore
                    )
                }
            } catch (e: Exception) {
                _activityListState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
