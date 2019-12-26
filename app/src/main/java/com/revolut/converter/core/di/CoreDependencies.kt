package com.revolut.converter.core.di

import android.content.Context
import com.revolut.converter.core.data.AppVisibility
import com.revolut.converter.core.data.InternetConnection
import com.revolut.converter.core.model.SchedulerProvider
import retrofit2.Retrofit

interface CoreDependencies {
    val retrofit: Retrofit
    val context: Context
    val appVisibility: AppVisibility
    val internetConnection: InternetConnection
    val schedulers: SchedulerProvider
}
