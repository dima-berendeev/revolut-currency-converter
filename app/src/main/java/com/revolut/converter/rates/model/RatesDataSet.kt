package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.Completable
import io.reactivex.Single
import java.math.BigDecimal

interface RatesDataSet {
    fun save(baseCurrency: CurrencyCode, currencyMap: Map<CurrencyCode, BigDecimal>): Completable
    fun get(baseCurrency: CurrencyCode): Single<Map<CurrencyCode, BigDecimal>>
    fun getCacheSize(): Single<Int>
}
