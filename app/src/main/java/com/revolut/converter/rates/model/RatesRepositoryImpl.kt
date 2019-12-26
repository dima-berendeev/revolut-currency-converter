package com.revolut.converter.rates.model

import com.revolut.converter.core.data.AppVisibility
import com.revolut.converter.core.data.InternetConnection
import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.Flowable
import io.reactivex.Observable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class RatesRepositoryImpl @Inject constructor(
    private val loadRates: LoadRates,
    private val internetConnection: InternetConnection,
    private val appVisibility: AppVisibility,
    private val ratesDataSet: RatesDataSet
) : RatesRepository {

    private val currencyRateCache = AtomicReference<RatesCache>()

    override fun observeRates(baseCurrency: CurrencyCode): Observable<RatesRepository.Result> {
        return appVisibility.observe()
            .switchMap { visibility: AppVisibility.State ->
                when (visibility) {
                    AppVisibility.State.APP_FOREGROUND -> getRatesObservableForVisible(baseCurrency)
                    AppVisibility.State.APP_BACKGROUND -> Observable.empty()
                }
            }
    }

    private fun getRatesObservableForVisible(baseCurrency: CurrencyCode): Observable<RatesRepository.Result> {
        val cachedValue: RatesCache? = currencyRateCache.get()
        val startWith = if (cachedValue?.baseCurrency == baseCurrency) {
            listOf(
                RatesRepository.Result(
                    baseCurrency = cachedValue.baseCurrency,
                    rates = cachedValue.ratioMap,
                    isOfflineResult = false
                )
            )
        } else {
            emptyList()
        }
        return internetConnection.observe()
            .switchMap { internetState ->
                when (internetState) {
                    InternetConnection.State.CONNECTED -> remoteObservable(baseCurrency)
                    InternetConnection.State.DISCONNECTED -> localObservable(baseCurrency)
                }
            }
            .startWith(startWith)
    }

    private fun remoteObservable(baseCurrency: CurrencyCode): Observable<RatesRepository.Result> {
        return loadRates.getSingle(baseCurrency)
            .retryWhen { flowable: Flowable<Throwable> -> flowable.delay(1, TimeUnit.SECONDS) }
            .repeatWhen { flowable -> flowable.delay(1, TimeUnit.SECONDS) }
            .toObservable()
            .map { RatesRepository.Result(it.base, it.rates, false) }
            .doOnNext { result -> saveRateCache(result) }
    }

    private fun saveRateCache(result: RatesRepository.Result) {
        currencyRateCache.set(
            RatesCache(
                baseCurrency = result.baseCurrency,
                ratioMap = result.rates
            )
        )
    }

    private fun localObservable(baseCurrency: CurrencyCode): Observable<RatesRepository.Result> {
        return ratesDataSet.get(baseCurrency).toObservable()
            .map { ratesMap ->
                RatesRepository.Result(
                    baseCurrency = baseCurrency,
                    rates = ratesMap,
                    isOfflineResult = true
                )
            }
            .doOnNext { result -> saveRateCache(result) }
    }

    private data class RatesCache(
        val baseCurrency: CurrencyCode,
        val ratioMap: Map<CurrencyCode, BigDecimal>
    )
}
