package com.revolut.converter.rates.data

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RatesBackendApi {
    @GET("latest")
    @Headers("Cache-Control: no-cache")
    fun getCurrencyRates(@Query("base") base: String?): Single<CurrenciesRates>

    data class CurrenciesRates(
        @SerializedName("base")
        val base: String,

        @SerializedName("date")
        val date: String,

        @SerializedName("rates")
        val rates: Map<String, String>
    )
}
