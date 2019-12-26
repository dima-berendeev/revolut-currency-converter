package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.Observable

interface CurrenciesOrderDataSet {
    fun save(currencyList: List<CurrencyCode>)
    fun observe(): Observable<List<CurrencyCode>>
    fun get(): List<CurrencyCode>
}
