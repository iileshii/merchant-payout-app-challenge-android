package com.example.androidinterview.data.api

import com.example.androidinterview.data.api.model.Activities
import com.example.androidinterview.data.api.model.MerchantResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MerchantService {

    @GET("api/merchant")
    suspend fun getMerchant(): MerchantResponse

    @GET("api/merchant/activity")
    suspend fun getActivity(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 15
    ): Activities
}
