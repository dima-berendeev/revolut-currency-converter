package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.math.BigDecimal
import javax.inject.Inject


class RatesModel @Inject constructor(
    private val ratesRepository: RatesRepository,
    private val currenciesOrderDataSet: CurrenciesOrderDataSet,
    private val baseCurrencyDataSet: BaseCurrencyDataSet
) {


    fun observe(): Observable<Result> {
        return Observables.combineLatest(
            baseCurrencyDataSet.observe(),
            getCurrencyRatesObservable(),
            currenciesOrderDataSet.observe()
        ) { currencyInfo, ratesInfo, currencyOrder ->
            if (currencyInfo.code != ratesInfo.base) {
                Observable.empty()
            } else {
                Observable.just(
                    mapResult(
                        currencyInfo,
                        ratesInfo.rates,
                        currencyOrder,
                        ratesInfo.isOfflineResult
                    )
                )
            }
        }.concatMap { it }
    }

    private fun getCurrencyRatesObservable(): Observable<RatesInfo> {
        return baseCurrencyDataSet.observe()
            .map { currencyInfo -> currencyInfo.code }
            .distinctUntilChanged()
            .switchMap { currencyCode ->
                ratesRepository.observeRates(currencyCode)
                    .map { ratesResult ->
                        RatesInfo(currencyCode, ratesResult.rates, ratesResult.isOfflineResult)
                    }
            }
    }

    private fun mapResult(
        baseCurrencyInfo: CurrencyInfo,
        rates: Map<CurrencyCode, BigDecimal>,
        userCurrencyOrder: List<CurrencyCode>,
        isOfflineResult: Boolean
    ): Result {
        val userOrderMap: Map<CurrencyCode, Int> = userCurrencyOrder.mapIndexed { index, code ->
            code to index
        }.toMap()

        val values: List<CurrencyInfo> = rates.mapValues { rateEntry ->
            val ratioToBaseCurrency = rateEntry.value
            (ratioToBaseCurrency * baseCurrencyInfo.amount).setScale(2, BigDecimal.ROUND_HALF_UP)
        }.map { entry ->
            CurrencyInfo(
                entry.key,
                entry.value
            )
        } + baseCurrencyInfo

        val comparator = compareBy<CurrencyInfo> { currencyInfo ->
            userOrderMap[currencyInfo.code] ?: Int.MAX_VALUE
        }.thenBy { currencyInfo -> currencyInfo.code.asString }
        val sortedValues = values.sortedWith(comparator)
        return Result(values = sortedValues, isOfflineMode = isOfflineResult)
    }

    data class Result(val values: List<CurrencyInfo>?, val isOfflineMode: Boolean)

    data class RatesInfo(
        val base: CurrencyCode,
        val rates: Map<CurrencyCode, BigDecimal>,
        val isOfflineResult: Boolean
    )
}
