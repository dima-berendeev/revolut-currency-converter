package com.revolut.converter.rates.model

import io.reactivex.Observable

interface BaseCurrencyDataSet {
    fun observe(): Observable<CurrencyInfo>
    fun set(currencyInfo: CurrencyInfo)
}
