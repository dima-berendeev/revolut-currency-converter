package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import javax.inject.Inject

class MoveCurrencyFirst @Inject constructor(private val currenciesLocalOrderDataSet: CurrenciesLocalOrderDataSet) {
    fun invoke(currency: CurrencyCode) {
        val mutableList = currenciesLocalOrderDataSet.get().toMutableList()
        mutableList.removeAll { it == currency }
        mutableList.add(0, currency)
        currenciesLocalOrderDataSet.save(mutableList)
    }
}
