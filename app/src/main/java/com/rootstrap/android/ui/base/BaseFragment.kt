package com.rootstrap.android.ui.base

import androidx.fragment.app.Fragment
import com.rootstrap.android.util.DialogUtil

open class BaseFragment : Fragment(), BaseView {

    override fun showProgress() {
        DialogUtil.showProgress(requireContext())
    }

    override fun hideProgress() {
        DialogUtil.hideProgress()
    }

    override fun showError(message: String?) {
        DialogUtil.showError(requireContext(), message)
    }
}
