package com.revolut.converter.testapp

import com.revolut.converter.core.common.SchedulerProvider
import io.reactivex.schedulers.TestScheduler

class TestSchedulers(testScheduler: TestScheduler) :
    SchedulerProvider {
    override val io = testScheduler
    override val computation = testScheduler
    override val ui = testScheduler
}
