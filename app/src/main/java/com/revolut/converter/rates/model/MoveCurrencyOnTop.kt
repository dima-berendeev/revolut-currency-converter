package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import javax.inject.Inject

class MoveCurrencyFirst @Inject constructor(private val currenciesOrderDataSet: CurrenciesOrderDataSet) {
    fun invoke(currency: CurrencyCode) {
        val mutableList = currenciesOrderDataSet.get().toMutableList()
        mutableList.removeAll { it == currency }
        mutableList.add(0, currency)
        currenciesOrderDataSet.save(mutableList)
    }
}
