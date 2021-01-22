package com.rootstrap.android.util.extensions

import android.app.Activity
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.run {
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}
