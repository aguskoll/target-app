package com.rootstrap.android.util

import android.app.AlertDialog
import android.content.Context
import com.rootstrap.android.R
import com.rootstrap.android.ui.custom.LoadingDialog

private var loadingDialog: LoadingDialog? = null

object DialogUtil {

    fun showProgress(context: Context) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(context, null)
        }

        loadingDialog!!.show()
    }

    fun hideProgress() {
        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
        }
    }

    fun showError(context: Context, message: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.error))

        when (message) {
            "" -> builder.setMessage(context.getString(R.string.generic_error))
            null -> builder.setMessage(context.getString(R.string.generic_error))
            else -> builder.setMessage(message)
        }

        builder.setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
            dialog.cancel()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
