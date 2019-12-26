package com.revolut.converter.core.model

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SchedulerProviderImpl @Inject constructor() : SchedulerProvider {
    override val io = Schedulers.io()
    override val computation = Schedulers.computation()
    override val ui = AndroidSchedulers.mainThread()
}
