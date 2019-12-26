package com.revolut.converter.rates.di

import com.revolut.converter.core.di.CoreDependencies
import com.revolut.converter.rates.data.*
import com.revolut.converter.rates.model.*
import com.revolut.converter.rates.presentation.RatesViewModel
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Component(dependencies = [CoreDependencies::class], modules = [RatesModule::class])
@Singleton
interface RatesComponent {
    val ratesViewModel: RatesViewModel

    @Component.Factory
    interface Factory {
        fun create(coreDependencies: CoreDependencies): RatesComponent
    }
}

@Module
abstract class RatesModule {
    @Binds
    abstract fun bindBaseCurrencyDataSet(impl: BaseCurrencyDataSetImpl): BaseCurrencyDataSet

    @Binds
    abstract fun bindLoadRates(impl: LoadRatesImpl): LoadRates

    @Binds
    abstract fun bindRatesRepository(impl: RatesRepositoryImpl): RatesRepository

    @Binds
    abstract fun bindRatesDataSet(impl: RatesDataSetImpl): RatesDataSet

    @Binds
    abstract fun bindCurrenciesOrderDataSet(impl: CurrenciesOrderDataSetImpl): CurrenciesOrderDataSet

    @Binds
    abstract fun bindRecursiveRatesCacheUpdater(impl: RecursiveRatesCacheUpdaterImpl): RecursiveRatesCacheUpdater

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideRatesApi(retrofit: Retrofit): RatesBackendApi {
            return retrofit.create(RatesBackendApi::class.java)
        }
    }
}
