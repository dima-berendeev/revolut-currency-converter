package com.revolut.converter.rates.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.revolut.converter.core.di.coreDependencies
import com.revolut.converter.rates.di.DaggerRatesComponent

internal class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DaggerRatesComponent.factory().create(coreDependencies).ratesViewModel as T
    }
}
