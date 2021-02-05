package com.rootstrap.android.util

import android.text.Editable

object Util {

    fun createEditable(text: String): Editable =
        Editable.Factory.getInstance().newEditable(text)

    fun createEmptyEditable(): Editable =
        createEditable("")
}
