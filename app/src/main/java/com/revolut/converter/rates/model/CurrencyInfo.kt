package com.revolut.converter.rates.model

import com.revolut.converter.rates.type.CurrencyCode
import java.math.BigDecimal

data class CurrencyInfo(val code: CurrencyCode, val amount: BigDecimal)
