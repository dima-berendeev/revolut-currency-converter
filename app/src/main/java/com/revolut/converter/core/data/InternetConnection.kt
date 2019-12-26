package com.revolut.converter.core.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.revolut.converter.core.model.SchedulerProvider
import com.revolut.converter.util.ConnectivityUtils
import com.revolut.converter.util.logDebug
import com.revolut.converter.util.requiredAllBranches
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import javax.inject.Inject
import javax.inject.Singleton

private const val INTERNET_CONNECTION_TAG = "INTERNET_CONNECTION"

interface InternetConnection {

    fun observe(): Observable<State>

    enum class State {
        CONNECTED,
        DISCONNECTED
    }
}

@Singleton
internal class InternetConnectionImpl @Inject constructor(
    private val context: Context,
    private val schedulers: SchedulerProvider
) :
    InternetConnection {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private val subscription = createSubscription()
        .subscribeOn(schedulers.io)
        .observeOn(schedulers.io)
        .doOnNext { state -> logState(state) }
        .replay(1)
        .autoConnect()

    override fun observe(): Observable<InternetConnection.State> {
        return subscription
    }

    private fun createSubscription(): Observable<InternetConnection.State> {
        return Observable.create<InternetConnection.State> { e: ObservableEmitter<InternetConnection.State> ->

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            if (!ConnectivityUtils.isConnectionOn(context)) {
                e.onNext(InternetConnection.State.DISCONNECTED)
            }
            val callback = object : ConnectivityManager.NetworkCallback() {
                val networkSet = mutableSetOf<Network>()
                override fun onAvailable(network: Network) {
                    if (isWiFiOrCell(network)) {
                        networkSet.add(network)
                        e.onNext(InternetConnection.State.CONNECTED)
                    }
                }

                override fun onLost(network: Network) {
                    networkSet.remove(network)
                    if (networkSet.isEmpty()) {
                        e.onNext(InternetConnection.State.DISCONNECTED)
                    }
                }
            }

            connectivityManager.registerNetworkCallback(request, callback)
        }
    }

    private fun isWiFiOrCell(network: Network): Boolean {
        val connection = connectivityManager.getNetworkCapabilities(network)
        return connection != null && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun logState(state: InternetConnection.State) {
        when (state) {
            InternetConnection.State.CONNECTED -> logDebug(
                INTERNET_CONNECTION_TAG, "Connected"
            )
            InternetConnection.State.DISCONNECTED -> logDebug(
                INTERNET_CONNECTION_TAG, "Disconnected"
            )
        }.requiredAllBranches()
    }
}


