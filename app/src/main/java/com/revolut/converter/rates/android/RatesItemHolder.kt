package com.revolut.converter.rates.android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.revolut.converter.R
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.rates.viewmodel.RatesViewState
import com.revolut.converter.util.hideSoftKeyboard
import com.revolut.converter.util.load
import com.revolut.converter.util.onTextChanged
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.converter_item.*
import java.text.DecimalFormatSymbols

class RatesItemHolder(
    parent: ViewGroup,
    private val callback: Callback
) : RecyclerView.ViewHolder(createView(parent)),
    LayoutContainer {

    private val decSeparator = DecimalFormatSymbols().decimalSeparator

    override val containerView: View
        get() = itemView

    private var isBindPerforming = false
    private var isSelfChanging = false
    private var currency: CurrencyCode? = null

    init {
        amountField.onTextChanged {
            if (!isBindPerforming && !isSelfChanging) {
                ensureTextInvariant()
                callback.onAmountChanged(currency!!, amountField.text.toString())
            }
        }
        itemView.setOnClickListener {
            callback.onItemClicked(currency!!)
        }
        itemView.isFocusableInTouchMode = false
        itemView.isFocusable = false

        amountField.setTextIsSelectable(false)
    }

    private fun ensureTextInvariant() {
        val text = amountField.text.toString()
        when (text) {
            decSeparator.toString() -> amountField.setText("0")
            "" -> amountField.setText("0")
        }
        if (text.length == 2 && text[0] == '0' && text[1] != decSeparator) {
            amountField.setText(text[1].toString())
        }
    }

    fun onDetach() {
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

        if (amountField.text.toString().toBigDecimalOrNull() != item.amount.toBigDecimalOrNull()) {
            amountField.setText(item.amount)
        }

        isBindPerforming = false
        currencyImage.load(item.currencyIconUrl)
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
