package com.rootstrap.android.ui.activity.main.targetpoint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rootstrap.android.R

class CreateTargetFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_create_target, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            CreateTargetFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
