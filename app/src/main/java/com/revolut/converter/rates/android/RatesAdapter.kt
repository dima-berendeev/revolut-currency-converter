package com.revolut.converter.rates.android

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.rates.viewmodel.RatesViewState

class RatesAdapter(private val callback: Callback) :
    ListAdapter<RatesViewState.Item, RatesItemHolder>(DiffCallback()) {
    private val itemCallback = ItemCallback()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesItemHolder {
        return RatesItemHolder(parent, itemCallback)
    }

    override fun onViewDetachedFromWindow(holder: RatesItemHolder) {
        holder.onDetach()
    }

    override fun onBindViewHolder(holder: RatesItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: RatesItemHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bind(getItem(position))
    }

    interface Callback {
        fun onAmountChanged(currency: CurrencyCode, value: String)
        fun onItemClicked(currency: CurrencyCode)
        fun onLostFocus()
    }

    private inner class ItemCallback : RatesItemHolder.Callback {
        override fun onAmountChanged(currency: CurrencyCode, amount: String) {
            callback.onAmountChanged(currency, amount)
        }

        override fun onItemClicked(currency: CurrencyCode) {
            callback.onItemClicked(currency)
        }

        override fun onLostFocus() {
            callback.onLostFocus()
        }
    }

    class DiffCallback() : DiffUtil.ItemCallback<RatesViewState.Item>() {


        override fun areItemsTheSame(
            oldItem: RatesViewState.Item,
            newItem: RatesViewState.Item
        ): Boolean {
            return oldItem.currencyCode == newItem.currencyCode
        }

        override fun areContentsTheSame(
            oldItem: RatesViewState.Item,
            newItem: RatesViewState.Item
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(
            oldItem: RatesViewState.Item,
            newItem: RatesViewState.Item
        ): Any? {
            return Unit
        }
    }
}
