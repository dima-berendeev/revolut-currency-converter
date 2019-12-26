package com.revolut.converter.core.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.revolut.converter.R
import com.revolut.converter.rates.android.RatesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.content, RatesFragment())
                .commit()
        }
    }
}
