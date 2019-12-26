package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.Single
import java.math.BigDecimal

interface LoadRates {
    fun getSingle(baseCurrency: CurrencyCode?): Single<Result>

    data class Result(
        val base: CurrencyCode,
        val rates: Map<CurrencyCode, BigDecimal>
    )
}
