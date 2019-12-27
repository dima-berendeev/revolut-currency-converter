package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.Observable
import io.reactivex.observables.GroupedObservable
import io.reactivex.rxkotlin.Observables
import java.math.BigDecimal
import javax.inject.Inject


class RatesModel @Inject constructor(
    private val ratesRepository: RatesRepository,
    private val currenciesLocalOrderDataSet: CurrenciesLocalOrderDataSet,
    private val baseCurrencyDataSet: BaseCurrencyDataSet
) {
    fun observe(): Observable<Result> {

        val groupedByCurrencyAmount: Observable<Observable<BigDecimal>> =
            baseCurrencyDataSet.observe()
                .groupBy { currencyInfo -> currencyInfo.code }
                .map { groupedObservable -> groupedObservable.map { currencyInfo -> currencyInfo.amount } }

        val groupedByCurrencyRates: Observable<GroupedObservable<CurrencyCode, RatesInfo>> =
            baseCurrencyDataSet.observe()
                .distinctUntilChanged()
                .map { currencyInfo -> currencyInfo.code }
                .switchMap { currencyCode: CurrencyCode ->
                    ratesRepository.observeRates(currencyCode)
                        .doOnDispose { println("dispose") }
                        .map { ratesResult ->
                            RatesInfo(
                                currencyCode,
                                ratesResult.rates,
                                ratesResult.isOfflineResult
                            )
                        }
                }.groupBy { it.base }

        val amountPlusRatesInfoObservable = Observables.zip(
            groupedByCurrencyAmount,
            groupedByCurrencyRates
        ) { amountGroups: Observable<BigDecimal>, rateGroups: Observable<RatesInfo> ->
            Observables.combineLatest(
                amountGroups,
                rateGroups
            ) { amount, rates ->
                BaseAmountAndRates(amount, rates)
            }
        }.flatMap { it }

        return Observables.combineLatest(
            amountPlusRatesInfoObservable,
            currenciesLocalOrderDataSet.observe()
        ) { amountPlusRatesInfo, localCurrencyOrder ->
            mapResult(
                baseAmount = amountPlusRatesInfo.baseAmount,
                baseCurrency = amountPlusRatesInfo.ratesInfo.base,
                rates = amountPlusRatesInfo.ratesInfo.rates,
                isOfflineResult = amountPlusRatesInfo.ratesInfo.isOfflineResult,
                userCurrencyOrder = localCurrencyOrder
            )
        }
    }


    private fun mapResult(
        baseAmount: BigDecimal,
        baseCurrency: CurrencyCode,
        rates: Map<CurrencyCode, BigDecimal>,
        userCurrencyOrder: List<CurrencyCode>,
        isOfflineResult: Boolean
    ): Result {
        val localCurrencyOrderMap: Map<CurrencyCode, Int> =
            userCurrencyOrder.mapIndexed { index, code ->
            code to index
        }.toMap()

        //unsorted currency items
        val values: List<CurrencyInfo> = rates.mapValues { rateEntry ->
            val ratioToBaseCurrency = rateEntry.value
            (ratioToBaseCurrency * baseAmount).setScale(2, BigDecimal.ROUND_HALF_UP)
        }.map { entry ->
            CurrencyInfo(
                entry.key,
                entry.value
            )
        } + CurrencyInfo(baseCurrency, baseAmount)

        // sort currencies
        val comparator = compareBy<CurrencyInfo> { currencyInfo ->
            localCurrencyOrderMap[currencyInfo.code] ?: Int.MAX_VALUE
        }.thenBy { currencyInfo -> currencyInfo.code.asString }
        val sortedValues = values.sortedWith(comparator)

        return Result(values = sortedValues, isOfflineMode = isOfflineResult)
    }

    data class Result(val values: List<CurrencyInfo>?, val isOfflineMode: Boolean)

    data class BaseAmountAndRates(val baseAmount: BigDecimal, val ratesInfo: RatesInfo)
    data class RatesInfo(
        val base: CurrencyCode,
        val rates: Map<CurrencyCode, BigDecimal>,
        val isOfflineResult: Boolean
    )
}
