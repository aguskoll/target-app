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
import com.rootstrap.android.models.TargetModel
import com.rootstrap.android.models.TopicModel
import com.rootstrap.android.ui.adapter.TopicAdapter
import com.rootstrap.android.util.extensions.value
import kotlinx.android.synthetic.main.fragment_create_target.view.*
import kotlinx.android.synthetic.main.layout_topics.view.*

class CreateTargetView(
    private val binding: ActivityTargetPointsBinding,
    private val targetPointsViewModel: TargetPointsViewModel,
    private val lifecycleOwner: LifecycleOwner
) {

    private val bindingRoot = binding.root

    private val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> =
        BottomSheetBehavior.from<ConstraintLayout>(bindingRoot.create_target_bottom_sheet)

    private val topicsBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> =
        BottomSheetBehavior.from<ConstraintLayout>(bindingRoot.select_topic_layout)

    private lateinit var topicAdapter: TopicAdapter

    private var selectedTopic: TopicModel? = null

    init {

        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            peekHeight = TargetPointsActivity.PICK_HEIGHT_HIDDEN
        }

        initTopicsBottomSheet()

        bindingRoot.apply {
            topic_edit_text.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    expandTopic()
            }
            topic_edit_text.setOnClickListener {
                expandTopic()
            }
            save_target_btn.setOnClickListener {
                createTarget()
            }
        }

        getTopics()

        initTopicsFilter()
    }

    private fun getTopics() {
        targetPointsViewModel.getTopics().observe(lifecycleOwner, Observer {
            initTopicList(it)
        })
    }

    private fun initTopicsBottomSheet() {
        topicsBottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN

            peekHeight = TargetPointsActivity.PICK_HEIGHT_HIDDEN

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    bindingRoot.select_topic_layout.progress = slideOffset
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
            })
        }
    }

    private fun initTopicList(topics: List<TopicModel>) {
        topicAdapter = TopicAdapter(topics) {
            selectedTopic(it)
        }
        with(bindingRoot.topics_recycler_view) {
            adapter = topicAdapter
            layoutManager = LinearLayoutManager(bindingRoot.context)
        }
    }

    private fun selectedTopic(topic: TopicModel) {
        selectedTopic = topic
        with(bindingRoot) {
            topic_edit_text.text = Editable.Factory.getInstance().newEditable(topic.label.name.capitalize())
            topicAdapter.clearFilter()
            filter_edit_text.text = Editable.Factory.getInstance().newEditable("")
        }
        collapseTopic()
    }

    private fun createTarget() {
        with(targetPointsViewModel) {
            val title = bindingRoot.title_edit_text.value()
            val area: Double = bindingRoot.area_edit_text.value().toDoubleOrNull() ?: 0.0
            val lat = getLocationLatitude()
            val lng = getLocationLongitude()
            val target = TargetModel(title, lat, lng, area, selectedTopic)

            if (isLocationStateSuccess() &&
                validateUserInputs(area, title, selectedTopic)
            ) {
                createTarget(target)
            }
        }
    }

    private fun validateUserInputs(area: Double, title: String?, topic: TopicModel?): Boolean =
        validateArea(area) && validateTargetTitle(title) && validateTopic(topic)

    private fun validateArea(area: Double): Boolean {
        val isAreaValid = targetPointsViewModel.isAreaValid(area)
        if (isAreaValid.not())
            bindingRoot.area_text_input_layout.error = SHOW_EMPTY_ERROR
        return isAreaValid
    }

    private fun validateTargetTitle(title: String?): Boolean {
        val isTitleValid = targetPointsViewModel.isTitleValid(title)
        if (isTitleValid.not()) {
            bindingRoot.title_text_input_layout.error = SHOW_EMPTY_ERROR
        }
        return isTitleValid
    }

    private fun validateTopic(topic: TopicModel?): Boolean {
        val isTopicValid = targetPointsViewModel.isTopicValid(topic)
        if (isTopicValid.not()) {
            bindingRoot.topic_text_input_layout.error = SHOW_EMPTY_ERROR
        }
        return isTopicValid
    }

    fun expandCollapseCreateTargetSheet() {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun expandTopic() {
        with(topicsBottomSheetBehavior) {
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
            halfExpandedRatio = TOPIC_SHEET_EXPANDED_RATIO
        }
    }

    private fun collapseTopic() {
        topicsBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun initTopicsFilter() {
        bindingRoot.filter_edit_text.addTextChangedListener(object : TextWatcher {
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
        const val TOPIC_SHEET_EXPANDED_RATIO = 0.70f
    }
}
