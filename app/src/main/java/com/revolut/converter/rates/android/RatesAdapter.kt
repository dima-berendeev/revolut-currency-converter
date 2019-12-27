package com.revolut.converter.rates.android

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.rates.viewmodel.RatesViewState
import com.revolut.converter.util.logDebug

class RatesAdapter(private val callback: Callback) : RecyclerView.Adapter<RatesItemHolder>() {
    private var items: List<RatesViewState.Item> = emptyList()

    fun update(newItems: List<RatesViewState.Item>) {
        logDebug("RATES_ADAPTER", "Update items")
        val oldItems = items
        items = newItems
        DiffUtil.calculateDiff(DiffCallback(oldItems, newItems))
            .dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesItemHolder {
        return RatesItemHolder(parent, ItemCallback())
    }

    override fun onViewDetachedFromWindow(holder: RatesItemHolder) {
        holder.onDetach()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RatesItemHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(
        holder: RatesItemHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bind(items[position])
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

    private class DiffCallback(
        private val oldItems: List<RatesViewState.Item>,
        private val newItems: List<RatesViewState.Item>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].currencyCode == newItems[newItemPosition].currencyCode
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return Unit
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }
}
