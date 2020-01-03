package com.revolut.converter.rates.android

import android.text.Editable
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
        amountField.addTextChangedListener(AmountTextWatcher())
        itemView.setOnClickListener {
            callback.onItemClicked(currency!!)
        }
        itemView.isFocusableInTouchMode = false
        itemView.isFocusable = false

        amountField.setTextIsSelectable(false)
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

        if (amountField.text.isEmpty() || !item.isBaseCurrency) {
            amountField.setText(item.amount)
        }

        isBindPerforming = false
        currencyImage.load(item.currencyIconUrl)
    }

    inner class AmountTextWatcher : TextWatcher {
        lateinit var beforeChangeText: String

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (isSelfChanging || isBindPerforming) return
            beforeChangeText = amountField.text.toString()
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (isSelfChanging || isBindPerforming) return
            isSelfChanging = true
            isSelfChanging = false
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
