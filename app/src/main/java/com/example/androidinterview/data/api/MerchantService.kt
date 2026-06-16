package com.example.androidinterview.data.api

import com.example.androidinterview.data.api.model.Activities
import com.example.androidinterview.data.api.model.MerchantResponse
import com.example.androidinterview.data.api.model.PayoutRequest
import com.example.androidinterview.data.api.model.PayoutResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MerchantService {

    @GET("api/merchant")
    suspend fun getMerchant(): MerchantResponse

    @GET("api/merchant/activity")
    suspend fun getActivity(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 15
    ): Activities

    @POST("api/payouts")
    suspend fun createPayout(@Body request: PayoutRequest): PayoutResponse

    @GET("api/payouts/{id}")
    suspend fun getPayout(@Path("id") id: String): PayoutResponse
}
