package com.revolut.converter.rates.model

import com.revolut.converter.core.data.AppVisibility
import com.revolut.converter.core.data.InternetConnection
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.util.logDebug
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val RECURSIVE_RATES_CACHE_UPDATER_TAG = "RECURSIVE_RATES_CACHE_UPDATER"

class RecursiveRatesCacheUpdaterImpl @Inject constructor(
    private val loadRates: LoadRates,
    private val internetConnection: InternetConnection,
    private val appVisibility: AppVisibility,
    private val ratesDataSet: RatesDataSet
) : RecursiveRatesCacheUpdater {

    override fun getCompletable(): Completable {
        return Observables.combineLatest(
            appVisibility.observe(),
            internetConnection.observe()
        ) { visibility, connection ->
            visibility == AppVisibility.State.APP_FOREGROUND && connection == InternetConnection.State.CONNECTED
        }.switchMapCompletable { updatePossible ->
            if (updatePossible) {
                ratesDataSet.getCacheSize()
                    .flatMapCompletable { cacheSize ->
                        val delayMs = if (cacheSize < 1) 100L else 2500L
                        getUpdateAllCurrenciesInDbCompletable(delayMs)
                    }
            } else {
                Completable.complete()
            }
        }
    }


    private fun getUpdateAllCurrenciesInDbCompletable(delayMs: Long): Completable {
        return loadRates.getSingle(null)
            .delay(delayMs, TimeUnit.MILLISECONDS)
            .retry()
            .map { result -> mapToCurrencyInfo(result) }
            .flatMapObservable { currencyInfo ->
                Observable.fromIterable(currencyInfo.rates.keys)
                    .concatMapSingle { currencyCode ->
                        loadRates.getSingle(currencyCode)
                            .delay(delayMs, TimeUnit.MILLISECONDS)
                            .retry()
                            .map { mapToCurrencyInfo(it) }
                    }.startWith(currencyInfo)
            }.flatMapCompletable { currencyInfo ->
                ratesDataSet.save(currencyInfo.base, currencyInfo.rates)
            }.doOnError { error ->
                logDebug(
                    RECURSIVE_RATES_CACHE_UPDATER_TAG,
                    "save rates to db error ${error.message}"
                )
                error.printStackTrace()
            }

    }

    private fun mapToCurrencyInfo(result: LoadRates.Result): CurrencyInfo {
        return CurrencyInfo(
            base = result.base,
            rates = result.rates
        )
    }

    private data class CurrencyInfo(
        val base: CurrencyCode,
        val rates: Map<CurrencyCode, BigDecimal>
    )

}
