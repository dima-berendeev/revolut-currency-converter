package com.revolut.converter.core.android

import android.app.Application
import com.facebook.stetho.Stetho
import com.revolut.converter.core.data.AppVisibilityImpl
import com.revolut.converter.core.di.CoreDependenciesHolder
import com.revolut.converter.core.di.DaggerCoreDependenciesComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initThirdPartLibraries()
        initDi()
        registerActivityLifecycleCallbacks(AppVisibilityImpl)
    }

    private fun initThirdPartLibraries() {
        Stetho.initializeWithDefaults(this)
    }

    private fun initDi() {
        CoreDependenciesHolder.coreDependencies =
            DaggerCoreDependenciesComponent.factory().create(this)
    }
}
