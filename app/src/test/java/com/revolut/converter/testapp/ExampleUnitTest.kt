package com.revolut.converter.testapp

import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

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
    }
}
