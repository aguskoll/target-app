package com.rootstrap.android.ui.activity.main.targetpoint

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private var selectedTopic: Topic? = null

    init {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = TargetPointsActivity.PICK_HEIGHT_HIDDEN

        initTopicsBottomSheet()

        binding.root.topic_edit_text.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                expandTopic()
        }

        binding.root.topic_edit_text.setOnClickListener {
            expandTopic()
        }

        binding.root.save_target_btn.setOnClickListener {
            createTarget()
        }

        targetPointsViewModel.getTopics().observe(lifecycleOwner, Observer {
            initTopicList(it)
        })

        filterTopics()
    }

    private fun initTopicsBottomSheet() {
        topicsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        topicsBottomSheetBehavior.peekHeight = TargetPointsActivity.PICK_HEIGHT_HIDDEN

        topicsBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.root.select_topic_layout.progress = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
        })
    }

    private fun initTopicList(topics: List<Topic>) {
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
        collapseTopic()
    }

    private fun createTarget() {
        val title = binding.root.title_edit_text.value()
        val area: Double = binding.root.area_edit_text.value().toDoubleOrNull() ?: 0.0
        val topic: Int = selectedTopic?.id ?: 0
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

    private fun expandTopic() {
        topicsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        topicsBottomSheetBehavior.halfExpandedRatio = 0.70f
    }

    private fun collapseTopic() {
        topicsBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun filterTopics() {
        binding.root.filter_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ?: 0 >= MIN_CHAR_FILTER) {
                    topicAdapter.filter(s.toString())
                } else if (s?.length ?: 0 == 0) {
                    topicAdapter.clearFilter()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
    }

    companion object {
        const val SHOW_EMPTY_ERROR = " "
        const val MIN_CHAR_FILTER = 3
    }
}
