package com.revolut.converter.rates.viewmodel

import com.revolut.converter.rates.type.CurrencyCode

data class RatesViewState(
    val items: List<Item>?, val isOfflineMode: Boolean
) {
    data class Item(
        val currencyCode: CurrencyCode,
        val currencyName: String,
        val currencyIconUrl:String,
        val amount: String
    )
}
