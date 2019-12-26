package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.Observable
import java.math.BigDecimal

interface RatesRepository {
    fun observeRates(baseCurrency: CurrencyCode): Observable<Result>

    data class Result(
        val baseCurrency: CurrencyCode,
        val rates: Map<CurrencyCode, BigDecimal>,
        val isOfflineResult: Boolean
    )
}
