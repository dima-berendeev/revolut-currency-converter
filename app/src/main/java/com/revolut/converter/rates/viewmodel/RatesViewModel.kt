package com.revolut.converter.rates.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revolut.converter.core.model.SchedulerProvider
import com.revolut.converter.rates.model.*
import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesViewModel @Inject constructor(
    private val ratesModel: RatesModel,
    private val moveCurrencyFirst: MoveCurrencyFirst,
    private val recursiveRatesCacheUpdater: RecursiveRatesCacheUpdater,
    private val baseCurrencyDataSet: BaseCurrencyDataSet,
    private val schedulers: SchedulerProvider
) : ViewModel() {

    val liveData = MutableLiveData<RatesViewState>()

    private val disposables = CompositeDisposable()

    init {
        launchUiSubscription()
        launchOflineRatesUpdaterSubscription()
    }

    private fun launchOflineRatesUpdaterSubscription() {
        disposables.add(
            recursiveRatesCacheUpdater.getCompletable()
                .subscribeOn(schedulers.io)
                .subscribe()
        )
    }

    private fun launchUiSubscription() {
        val disposable = ratesModel.observe()
            .map { modelResult -> mapUiState(modelResult) }
            .subscribeOn(schedulers.io)
            .throttleLast(200, TimeUnit.MILLISECONDS)
            .subscribe { uiState ->
                updateUi(uiState)
            }
        disposables.add(disposable)
    }

    private fun updateUi(uiState: RatesViewState) {
        liveData.postValue(uiState)
    }

    private fun mapUiState(modelResult: RatesModel.Result): RatesViewState {
        val items = modelResult.values?.map { currencyInfo -> mapItem(currencyInfo) }
        return RatesViewState(items, modelResult.isOfflineMode)
    }

    private fun mapItem(currencyInfo: CurrencyInfo): RatesViewState.Item {
        return RatesViewState.Item(
            currencyCode = currencyInfo.code,
            currencyName = CurrencyDetails.currencyNames[currencyInfo.code] ?: "Unknown",
            amount = currencyInfo.amount.toString(),
            currencyIconUrl = getCurrencyIconUrl(currencyInfo.code)
        )
    }

    private fun getCurrencyIconUrl(currency: CurrencyCode): String {
        return "https://raw.githubusercontent.com/dima-berendeev/" +
                "revolut-currency-converter/master/currency-flags/${currency.asString}.png"
    }

    fun onAmountChanged(currency: CurrencyCode, value: String) {
        val baseCurrencyInfo =
            CurrencyInfo(currency, value.toBigDecimalOrNull() ?: 0.toBigDecimal())
        baseCurrencyDataSet.set(baseCurrencyInfo)
    }

    fun onCurrencyTaped(currency: CurrencyCode) {
        moveCurrencyFirst.invoke(currency)
    }
}
