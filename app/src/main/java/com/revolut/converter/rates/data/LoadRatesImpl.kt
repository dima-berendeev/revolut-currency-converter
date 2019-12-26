package com.revolut.converter.rates.data

import com.revolut.converter.rates.model.LoadRates
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.util.logDebug
import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

private const val LOAD_RATES_TAG = "LOAD_RATES"

class LoadRatesImpl @Inject constructor(private val ratesBackendApi: RatesBackendApi) :
    LoadRates {

    override fun getSingle(baseCurrency: CurrencyCode?): Single<LoadRates.Result> {
        logDebug(LOAD_RATES_TAG, "create single")
        return ratesBackendApi.getCurrencyRates(baseCurrency?.asString)
            .doOnSubscribe { logDebug(LOAD_RATES_TAG, "on start") }
            .map { dto -> mapResult(dto) }
    }


    private fun mapResult(dto: RatesBackendApi.CurrenciesRates): LoadRates.Result {
        return LoadRates.Result(
            base = CurrencyCode(dto.base),
            rates = dto.rates.map { rate -> mapRate(rate.key, rate.value) }.toMap()
        )
    }

    private fun mapRate(code: String, rate: String): Pair<CurrencyCode, BigDecimal> {
        return CurrencyCode(code) to rate.toBigDecimal()
    }

}

