package com.revolut.converter.core.di

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.revolut.converter.BuildConfig
import com.revolut.converter.core.data.*
import com.revolut.converter.core.model.SchedulerProvider
import com.revolut.converter.core.model.SchedulerProviderImpl
import com.revolut.converter.util.logDebug
import dagger.*
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Component(modules = [CoreDependenciesModule::class])
@Singleton
interface CoreDependenciesComponent : CoreDependencies {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CoreDependenciesComponent
    }
}

@Module
internal abstract class CoreDependenciesModule {
    @Binds
    @Singleton
    abstract fun bindSchedulerProvider(impl: SchedulerProviderImpl): SchedulerProvider

    @Binds
    @Singleton
    abstract fun bindInternetConnection(impl: InternetConnectionImpl): InternetConnection

    @Module
    companion object {
        @JvmStatic
        @Provides
        @Singleton
        fun provideAppVisibility(): AppVisibility {
            return AppVisibilityImpl
        }


        @JvmStatic
        @Provides
        @Singleton
        fun provideRetrofit(context: Context): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://revolut.duckdns.org")
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient(context))
                .build()

        }


        private fun getGson(): Gson {
            return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create()
        }

        private fun getOkHttpClient(context: Context): OkHttpClient {
            val logger = object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if (message.length > 40000) {
                        return
                    }
                    logDebug("NETWORK", message)
                }
            }

            val httpLoggingInterceptor = HttpLoggingInterceptor(logger)

            val loggerInterceptor = httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                // work around. okHttp looses network after network turn up/down .
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .addInterceptor(loggerInterceptor)
                .addInterceptor(NoConnectionInterceptor(context))
                // e-tag on server broken
                // api method always return 304
//                .cache(getCache(context))
                .build()
        }

        private fun getCache(context: Context): Cache {
            val cacheSize: Long = 10 * 1024 * 1024 // 10 MiB
            val cacheDirectory = File(context.cacheDir, "okhttp-cache")
            return Cache(cacheDirectory, cacheSize)
        }

    }


}
