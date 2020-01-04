package com.revolut.converter.core.common

import io.reactivex.Scheduler

interface SchedulerProvider {
    val io: Scheduler
    val computation: Scheduler
    val ui: Scheduler
}
