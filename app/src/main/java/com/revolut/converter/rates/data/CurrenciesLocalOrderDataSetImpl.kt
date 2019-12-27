package com.revolut.converter.rates.data

import android.content.Context
import android.preference.PreferenceManager
import com.revolut.converter.rates.model.CurrenciesLocalOrderDataSet
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.util.preferenceProperty
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrenciesLocalOrderDataSetImpl @Inject constructor(private val context: Context) :
    CurrenciesLocalOrderDataSet {

    private val preferences by lazy(LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    private var currenciesOrder by preferenceProperty(preferences, "currencies_order") { "" }

    private val subject by lazy { BehaviorSubject.createDefault(deserializeOrder(currenciesOrder)) }

    private fun deserializeOrder(serializedValue: String): List<CurrencyCode> {
        return if (serializedValue.isEmpty()) {
            emptyList()
        } else {
            serializedValue.split(",").map { CurrencyCode(it) }
        }
    }

    override fun save(currencyList: List<CurrencyCode>) {
        if (currencyList.toSet().size != currencyList.size) {
            throw IllegalStateException()
        }

        val serializedList = currencyList.joinToString(separator = ",") { it.asString }
        if (currenciesOrder != serializedList) {
            currenciesOrder = serializedList
            subject.onNext(currencyList)
        }
    }

    override fun observe(): Observable<List<CurrencyCode>> {
        return subject
    }

    override fun get(): List<CurrencyCode> {
        return deserializeOrder(currenciesOrder)
    }
}

