package com.revolut.converter.core.model

import io.reactivex.Scheduler

interface SchedulerProvider {
    val io: Scheduler
    val computation: Scheduler
    val ui: Scheduler
}
