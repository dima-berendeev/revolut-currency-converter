package com.revolut.converter.testapp

import com.revolut.converter.rates.model.*
import com.revolut.converter.rates.type.CurrencyCode
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class ExampleUnitTest {
    private lateinit var testScheduler: TestScheduler
    private lateinit var testSchedulers: TestSchedulers

    @Before
    fun before() {
        testScheduler = TestScheduler()
        testSchedulers = TestSchedulers(testScheduler)
    }

    @Test
    fun open_online_apiSuccess() {
        val ratesRepo = object : RatesRepository {
            override fun observeRates(baseCurrency: CurrencyCode): Observable<RatesRepository.Result> {
                val rates = mapOf(
                    CurrencyCode("RUB") to 70.1.toBigDecimal(),
                    CurrencyCode("USD") to 1.3.toBigDecimal()
                )

                val result = RatesRepository.Result(CurrencyCode("EUR"), rates, false)
                return Observable.just(result, result)
            }
        }

        val order = object : CurrenciesLocalOrderDataSet {
            override fun save(currencyList: List<CurrencyCode>) {
            }

            override fun observe(): Observable<List<CurrencyCode>> {
                return Observable.just<List<CurrencyCode>>(emptyList())
            }

            override fun get(): List<CurrencyCode> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        val baseCurrencyDataSet = object : BaseCurrencyDataSet {
            override fun observe(): Observable<CurrencyInfo> {
                return Observable.just(CurrencyInfo(CurrencyCode("EUR"), 100.toBigDecimal()))
            }

            override fun set(currencyInfo: CurrencyInfo) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        val values = listOf(
            CurrencyInfo(
                CurrencyCode("EUR"),
                100.toBigDecimal()
            ),

            CurrencyInfo(
                CurrencyCode("RUB"),
                7010.toBigDecimal().setScale(2)
            ),
            CurrencyInfo(
                CurrencyCode("USD"),
                130.toBigDecimal().setScale(2)
            )
        )

        val expectedResult = RatesModel.Result(values = values, isOfflineMode = false)

        val testScheduler = TestScheduler()

        val testObserver: TestObserver<RatesModel.Result> =
            RatesModel(ratesRepo, order, baseCurrencyDataSet).observe()
                .subscribeOn(testScheduler)
                .observeOn(testScheduler)
                .test()

        testScheduler.advanceTimeBy(1L, TimeUnit.MILLISECONDS);
        testObserver.assertValueCount(1)
        testObserver.assertValues(expectedResult)
        testObserver.dispose()
    }
}
