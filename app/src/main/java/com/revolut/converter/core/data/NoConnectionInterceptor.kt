package com.revolut.converter.core.data

import android.content.Context
import com.revolut.converter.util.ConnectivityUtils.isConnectionOn
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NoConnectionInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnectionOn(context)) {
            throw NoConnectivityException()
        } else {
            chain.proceed(chain.request())
        }
    }
}


class NoConnectivityException : IOException("No connectivity")
