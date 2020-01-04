package com.revolut.converter.rates.android

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.revolut.converter.R
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.rates.viewmodel.RatesViewState
import com.revolut.converter.util.hideSoftKeyboard
import com.revolut.converter.util.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.converter_item.*

private const val MAX_DIGITS_BEFORE_POINT = 8
private const val MAX_DIGITS_AFTER_POINT = 2

class RatesItemHolder(
    parent: ViewGroup,
    private val callback: Callback
) : RecyclerView.ViewHolder(createView(parent)),
    LayoutContainer {

    override val containerView: View
        get() = itemView

    private var isBindPerforming = false
    private var isSelfChanging = false
    private var currency: CurrencyCode? = null
    private var initializedAfterAttach = false

    init {
        amountField.addTextChangedListener(AmountTextWatcher())
        itemView.setOnClickListener {
            callback.onItemClicked(currency!!)
        }
        itemView.isFocusableInTouchMode = false
        itemView.isFocusable = false

        amountField.setTextIsSelectable(false)
    }


    fun onDetach() {
        initializedAfterAttach = false
        if (amountField.isFocused) {
            amountField.hideSoftKeyboard()
            callback.onLostFocus()
        }
    }

    fun bind(item: RatesViewState.Item) {
        isBindPerforming = true
        currency = item.currencyCode
        currencyCodeView.text = item.currencyCode.asString
        currencyCountryView.text = item.currencyName

        if (!item.isBaseCurrency || !initializedAfterAttach) {
            amountField.setText(item.amount)
        }

        isBindPerforming = false
        currencyImage.load(item.currencyIconUrl)
        initializedAfterAttach = true
    }

    inner class AmountTextWatcher : TextWatcher {
        private val decimalSeparator = Regex("[,.]")
        private val redundantNullPattern = "^0[0-9]$".toRegex()

        lateinit var beforeChangeText: String

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (isSelfChanging || isBindPerforming) return
            beforeChangeText = amountField.text.toString()
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (isSelfChanging || isBindPerforming) return
            isSelfChanging = true
            processChange(beforeChangeText, s)
            isSelfChanging = false
        }

        private fun processChange(before: String, after: CharSequence) {
            when {
                // replace empty with 0
                after.isEmpty() -> {
                    amountField.setText("0")
                }

                // replace 01 with 0
                before == "0" && after.matches(redundantNullPattern) -> {
                    amountField.setText("${after[1]}")
                }

                // not allow add excess digit
                TextUtils.isDigitsOnly(after) && after.length > before.length && after.length > MAX_DIGITS_BEFORE_POINT -> {
                    amountField.setText(before)
                }

            }

            //leave only 2 digits after point
            val parts = (after.split(decimalSeparator))
            if (parts.size == 2) {
                if (parts[1].length > MAX_DIGITS_AFTER_POINT) {
                    amountField.setText(before)
                }
            }

            callback.onAmountChanged(currency!!, amountField.text.toString())
        }

        override fun afterTextChanged(s: Editable?) {


        }
    }

    interface Callback {
        fun onAmountChanged(currency: CurrencyCode, amount: String)
        fun onItemClicked(currency: CurrencyCode)
        fun onLostFocus()
    }
}

private fun createView(parent: ViewGroup): View {
    return LayoutInflater.from(parent.context).inflate(R.layout.converter_item, parent, false)
}
