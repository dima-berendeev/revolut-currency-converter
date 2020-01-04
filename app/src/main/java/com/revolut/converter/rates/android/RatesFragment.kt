package com.revolut.converter.rates.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.revolut.converter.R
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.rates.viewmodel.RatesViewModel
import com.revolut.converter.rates.viewmodel.RatesViewState
import com.revolut.converter.util.arg
import kotlinx.android.synthetic.main.rates_fragment.*

class RatesFragment : Fragment() {

    private val adapter = RatesAdapter(RatesAdapterCallback())
    private lateinit var snackbar: Snackbar
    private var isUserDismissedSnackbar by arg<Boolean>()

    private val viewModel by lazy(mode = LazyThreadSafetyMode.NONE) { provideViewModel() }

    private fun provideViewModel(): RatesViewModel {
        return ViewModelProviders.of(this, ViewModelFactory()).get(RatesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            isUserDismissedSnackbar = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rates_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSnackbar()
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.liveData.observe(
            viewLifecycleOwner,
            Observer { viewState -> updateUi(viewState) })
    }

    private fun initSnackbar() {
        snackbar = Snackbar.make(
            recyclerView,
            getString(R.string.rates_snack_bar_offline_mode),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.rates_snack_bar_action_hide)) {
            isUserDismissedSnackbar = true
            snackbar.dismiss()
        }
    }

    private var lastItems: List<RatesViewState.Item>? = null

    private fun updateUi(viewState: RatesViewState) {
        when {
            !snackbar.isShown && !isUserDismissedSnackbar && viewState.isOfflineMode -> {
                snackbar.show()
            }
            snackbar.isShown && !viewState.isOfflineMode -> snackbar.dismiss()
        }
        val newItems = viewState.items
        if (newItems != null) {
            //workaround prevent shift items on moving currency item upward
            val ss = recyclerView.layoutManager!!.onSaveInstanceState()
            adapter.update(newItems)
            recyclerView.layoutManager!!.onRestoreInstanceState(ss)
            lastItems = newItems
            progressBar.visibility = View.GONE
        } else {
            progressBar.visibility = View.VISIBLE
        }
    }

    private inner class RatesAdapterCallback : RatesAdapter.Callback {
        override fun onAmountChanged(currency: CurrencyCode, value: String) {
            viewModel.onAmountChanged(currency, value)
        }

        override fun onItemClicked(currency: CurrencyCode) {
            viewModel.onCurrencyTaped(currency)
        }

        override fun onLostFocus() {
            frameView.requestFocus()
        }
    }
}
