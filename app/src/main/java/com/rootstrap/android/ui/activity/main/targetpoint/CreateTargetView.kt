package com.rootstrap.android.ui.activity.main.targetpoint

import android.text.Editable
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.crashlytics.internal.common.CommonUtils.hideKeyboard
import com.rootstrap.android.databinding.ActivityTargetPointsBinding
import com.rootstrap.android.network.models.Target
import com.rootstrap.android.network.models.Topic
import com.rootstrap.android.ui.adapter.TopicAdapter
import com.rootstrap.android.util.extensions.value
import kotlinx.android.synthetic.main.fragment_create_target.view.*

import kotlinx.android.synthetic.main.layout_topics.view.*

class CreateTargetView(
    private val binding: ActivityTargetPointsBinding,
    private val targetPointsViewModel: TargetPointsViewModel,
    private val lifecycleOwner: LifecycleOwner
) {

    private val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> =
        BottomSheetBehavior.from<ConstraintLayout>(binding.root.create_target_bottom_sheet)

    private val topicsBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> =
        BottomSheetBehavior.from<ConstraintLayout>(binding.root.select_topic_layout)

    private lateinit var topicAdapter: TopicAdapter

    private lateinit var selectedTopic: Topic

    init {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = TargetPointsActivity.PICK_HEIGHT_HIDDEN

        topicsBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        topicsBottomSheetBehavior.peekHeight = TargetPointsActivity.PICK_HEIGHT_HIDDEN

        binding.root.topic_edit_text.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                expandCollapseSelectTopic()
        }

        binding.root.topic_edit_text.setOnClickListener {
            expandCollapseSelectTopic()
        }

        binding.root.save_target_btn.setOnClickListener {
            createTarget()
        }

        targetPointsViewModel.getTopics().observe(lifecycleOwner, Observer {
            initTopics(it)
        })
    }

    private fun initTopics(topics: List<Topic>) {
        topicAdapter = TopicAdapter(topics) {
            selectedTopic(it)
        }
        with(binding.root.topics_recycler_view) {
            adapter = topicAdapter
            layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    private fun selectedTopic(topic: Topic) {
        selectedTopic = topic
        binding.root.topic_edit_text.text = Editable.Factory.getInstance().newEditable(topic.label)
        expandCollapseSelectTopic()
    }

    private fun createTarget() {
        val title = binding.root.title_edit_text.value()
        val area: Double = binding.root.area_edit_text.value().toDoubleOrNull() ?: 0.0
        val topic: Int = selectedTopic.id
        val lat = targetPointsViewModel.getLocationLatitude()
        val lng = targetPointsViewModel.getLocationLongitude()
        val target = Target(title, lat, lng, area, topic)

        if (targetPointsViewModel.isLocationStateSuccess() &&
            validateUserInputs(area, title, topic)
        ) {
            targetPointsViewModel.createTarget(target)
        }
    }

    private fun validateUserInputs(area: Double, title: String?, topic: Int): Boolean =
        validateArea(area) && validateTargetTitle(title) && validateTopic(topic)

    private fun validateArea(area: Double): Boolean {
        val isAreaValid = targetPointsViewModel.isAreaValid(area)
        if (isAreaValid.not())
            binding.root.area_text_input_layout.error = SHOW_EMPTY_ERROR
        return isAreaValid
    }

    private fun validateTargetTitle(title: String?): Boolean {
        val isTitleValid = targetPointsViewModel.isTitleValid(title)
        if (isTitleValid.not()) {
            binding.root.title_text_input_layout.error = SHOW_EMPTY_ERROR
        }
        return isTitleValid
    }

    private fun validateTopic(topic: Int): Boolean {
        val isTopicValid = targetPointsViewModel.isTopicValid(topic)
        if (isTopicValid.not()) {
            binding.root.topic_text_input_layout.error = SHOW_EMPTY_ERROR
        }
        return isTopicValid
    }

    fun expandCollapseCreateTargetSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun expandCollapseSelectTopic() {
        if (topicsBottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            topicsBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            hideKeyboard(binding.root.context, binding.root)
        } else {
            topicsBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    companion object {
        const val SHOW_EMPTY_ERROR = " "
    }
}
