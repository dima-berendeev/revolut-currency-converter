package com.revolut.converter.util

import android.util.Log
import com.revolut.converter.BuildConfig

fun logDebug(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message)
    }
}
