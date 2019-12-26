package com.revolut.converter.rates.model

import io.reactivex.Completable

interface RecursiveRatesCacheUpdater {
    fun getCompletable(): Completable
}
