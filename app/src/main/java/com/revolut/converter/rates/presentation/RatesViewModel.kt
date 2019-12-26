package com.revolut.converter.rates.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revolut.converter.core.model.SchedulerProvider
import com.revolut.converter.rates.model.*
import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.disposables.CompositeDisposable
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
        startUiSubscription()
        startUpdaterSubscription()
    }

    private fun startUpdaterSubscription() {
        disposables.add(
            recursiveRatesCacheUpdater.getCompletable()
                .subscribeOn(schedulers.io)
                .subscribe()
        )
    }

    private fun startUiSubscription() {
        val disposable = ratesModel.observe()
            .map { modelResult -> mapUiState(modelResult) }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { uiState ->
                liveData.value = uiState
            }
        disposables.add(disposable)
    }

    private fun mapUiState(modelResult: RatesModel.Result): RatesViewState {
        val items = modelResult.values?.map { currencyInfo -> mapItem(currencyInfo) }
        return RatesViewState(items, modelResult.isOfflineMode)
    }

    private fun mapItem(currencyInfo: CurrencyInfo): RatesViewState.Item {
        return RatesViewState.Item(
            currencyCode = currencyInfo.code,
            currencyName = "",
            amount = currencyInfo.amount.toString()
        )
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
