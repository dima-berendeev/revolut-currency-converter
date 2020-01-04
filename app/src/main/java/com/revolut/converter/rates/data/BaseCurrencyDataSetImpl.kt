package com.revolut.converter.rates.data

import android.content.Context
import android.preference.PreferenceManager
import com.revolut.converter.core.common.SchedulerProvider
import com.revolut.converter.rates.model.BaseCurrencyDataSet
import com.revolut.converter.rates.model.CurrencyInfo
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.util.preferenceProperty
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseCurrencyDataSetImpl @Inject constructor(
    private val context: Context,
    private val schedulers: SchedulerProvider
) : BaseCurrencyDataSet {

    private val preferences by lazy(LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    private var baseCurrencyCode by preferenceProperty(preferences, "base_currency_code") { "EUR" }
    private var baseCurrencyAmount by preferenceProperty(
        preferences,
        "base_currency_amount"
    ) { "100" }

    private val subject by lazy { BehaviorSubject.createDefault(deserializeBaseCurrency()) }

    private fun deserializeBaseCurrency(): CurrencyInfo {
        return CurrencyInfo(CurrencyCode(baseCurrencyCode), baseCurrencyAmount.toBigDecimal())
    }

    override fun observe(): Observable<CurrencyInfo> {
        return subject.observeOn(schedulers.io)
    }

    override fun set(currencyInfo: CurrencyInfo) {
        baseCurrencyAmount = currencyInfo.amount.toString()
        baseCurrencyCode = currencyInfo.code.asString
        subject.onNext(currencyInfo)
    }
}
