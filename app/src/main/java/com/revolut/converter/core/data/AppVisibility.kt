package com.revolut.converter.core.data

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.revolut.converter.core.di.coreDependencies
import com.revolut.converter.util.logDebug
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

private const val APP_VISIBILITY_TAG = "APP_VISIBILITY"

interface AppVisibility {
    fun observe(): Observable<State>

    enum class State {
        APP_FOREGROUND,
        APP_BACKGROUND
    }
}

internal object AppVisibilityImpl : Application.ActivityLifecycleCallbacks, AppVisibility {
    private val schedulers = coreDependencies.schedulers
    private val subject = BehaviorSubject.createDefault(AppVisibility.State.APP_BACKGROUND)
    private val subscription = subject.doOnNext { state -> logState(state) }
        .observeOn(schedulers.io)
        .replay(1)
        .autoConnect()

    private var counter = 0

    override fun onActivityStarted(activity: Activity) {
        counter++
        emmitVisibility()
    }

    override fun onActivityStopped(activity: Activity) {
        counter--
        emmitVisibility()
    }

    private fun emmitVisibility() {
        val state = if (counter > 0) {
            AppVisibility.State.APP_FOREGROUND
        } else {
            AppVisibility.State.APP_BACKGROUND
        }

        subject.onNext(state)
    }

    override fun observe(): Observable<AppVisibility.State> {
        return subscription
    }

    private fun logState(state: AppVisibility.State) {
        val message = when (state) {
            AppVisibility.State.APP_FOREGROUND -> "Foreground"
            AppVisibility.State.APP_BACKGROUND -> "Background"
        }
        logDebug(APP_VISIBILITY_TAG, message)
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
    }
}
