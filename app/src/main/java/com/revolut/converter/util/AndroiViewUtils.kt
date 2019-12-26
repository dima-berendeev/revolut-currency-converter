package com.revolut.converter.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.ViewCompat

fun EditText.onTextChanged(block: (String) -> Unit): TextWatcher {
    val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            block(s.toString())
        }
    }
    this.addTextChangedListener(textWatcher)
    return textWatcher
}

fun View.hideSoftKeyboard() {
    if (ViewCompat.isAttachedToWindow(this)) {
        val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}
