package com.rootstrap.android.ui.base

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.rootstrap.android.R
import com.rootstrap.android.ui.custom.LoadingDialog

open class BaseFragment : Fragment(), BaseView {

    private var loadingDialog: LoadingDialog? = null

    override fun showProgress() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(requireContext(), null)
        }

        loadingDialog!!.show()
    }

    override fun hideProgress() {
        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
        }
    }

    override fun showError(message: String?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.error))

        when (message) {
            "" -> builder.setMessage(getString(R.string.generic_error))
            null -> builder.setMessage(getString(R.string.generic_error))
            else -> builder.setMessage(message)
        }

        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.cancel()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
