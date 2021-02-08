package com.rootstrap.android.ui.activity.main.targetpoint

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.crashlytics.internal.common.CommonUtils.hideKeyboard
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ActivityTargetPointsBinding
import com.rootstrap.android.models.TargetModel
import com.rootstrap.android.models.TopicModel
import com.rootstrap.android.ui.adapter.TopicAdapter
import com.rootstrap.android.util.DialogUtil
import com.rootstrap.android.util.Util
import com.rootstrap.android.util.extensions.getIconForTarget
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

    private var selectedTarget: TargetModel? = null

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

            delete_target_small_btn.setOnClickListener {
                deleteTarget()
            }
            save_target_small_btn.setOnClickListener {
                expandCollapseCreateTargetSheet()
            }
        }

        getTopics()

        initTopicsFilter()

        observeCreateTargetState()

        observeShowTarget()

        observeDeleteTargetState()
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

    private fun observeShowTarget() {
        targetPointsViewModel.hasToShowTargetInformation().observe(lifecycleOwner, Observer {
            showTargetInformation(it)
        })
    }

    private fun selectedTopic(topic: TopicModel) {
        selectedTopic = topic
        with(bindingRoot) {
            topic_text_input_layout.error = null
            topicAdapter.clearFilter()
            filter_edit_text.text = Util.createEmptyEditable()
        }
        setTopicIcon(topic)
        collapseTopic()
    }

    private fun setTopicIcon(topic: TopicModel) {
        val textSpan = SpannableString(DOUBLE_EMPTY_SPACE_FOR_SPAN + topic.label.name)
        val iconDrawable: Drawable? = ContextCompat.getDrawable(bindingRoot.context, topic.getIconForTarget())
        iconDrawable?.setBounds(0, 0, 60, 60)

        iconDrawable?.run {
            val imageSpan = ImageSpan(iconDrawable, ImageSpan.ALIGN_CENTER)
            textSpan.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            val editable = Editable.Factory.getInstance().newEditable(textSpan)
            bindingRoot.topic_edit_text.text = editable
        }
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
        bindingRoot.area_text_input_layout.error = if (isAreaValid.not())
            SHOW_EMPTY_ERROR
        else null
        return isAreaValid
    }

    private fun validateTargetTitle(title: String?): Boolean {
        val isTitleValid = targetPointsViewModel.isTitleValid(title)
        bindingRoot.title_text_input_layout.error = if (isTitleValid.not()) {
            SHOW_EMPTY_ERROR
        } else null
        return isTitleValid
    }

    private fun validateTopic(topic: TopicModel?): Boolean {
        val isTopicValid = targetPointsViewModel.isTopicValid(topic)
        bindingRoot.topic_text_input_layout.error = if (isTopicValid.not()) {
            SHOW_EMPTY_ERROR
        } else null
        return isTopicValid
    }

    fun expandCollapseCreateTargetSheet() {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            showBottomButtons(true)
            eraseTargetInformation()
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun showBottomButtons(isCreateTarget: Boolean) {
        with(bindingRoot) {
            if (isCreateTarget) {
                small_buttons_container.visibility = View.GONE
                save_target_btn.visibility = View.VISIBLE
            } else {
                small_buttons_container.visibility = View.VISIBLE
                save_target_btn.visibility = View.GONE
            }
        }
    }

    private fun eraseTargetInformation() {
        with(bindingRoot) {
            title_edit_text.text = Util.createEmptyEditable()
            area_edit_text.text = Util.createEmptyEditable()
            topic_edit_text.text = Util.createEmptyEditable()
            text_select_topic.text = context.getString(R.string.select_a_topic)
            text_area_length.text = context.getString(R.string.specify_area_length)
        }
    }

    private fun showTargetInformation(targetModel: TargetModel) {
        bottomSheetBehavior.state = if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            completeTargetInformation(targetModel)
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun completeTargetInformation(targetModel: TargetModel) {
        with(bindingRoot) {
            selectedTarget = targetModel
            showBottomButtons(false)
            title_edit_text.text = Util.createEditable(targetModel.title)
            area_edit_text.text = Util.createEditable("" + targetModel.radius)
            targetModel.topic?.run {
                selectedTopic(this)
            }
            text_select_topic.text = context.getString(R.string.topic)
            text_area_length.text = context.getString(R.string.area_length)
        }
    }

    private fun deleteTarget() {
        selectedTarget?.run {
            targetPointsViewModel.deleteTarget(this)
            expandCollapseCreateTargetSheet()
        }
    }

    private fun observeDeleteTargetState() {
        targetPointsViewModel.deleteTargetState.observe(lifecycleOwner, Observer { state ->
            with(bindingRoot.context) {
                if (state == ActionOnTargetState.fail) {
                    showError(getString(R.string.failed_creating_target))
                }
            }
        })
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

    private fun observeCreateTargetState() {
        targetPointsViewModel.createTargetState.observe(lifecycleOwner, Observer { targetState ->
            targetState?.run {
                when (this) {
                    ActionOnTargetState.fail -> showError(
                        targetPointsViewModel.error ?: bindingRoot.context.getString(R.string.default_error)
                    )
                    ActionOnTargetState.success -> {
                        successCreatingTarget()
                    }
                    ActionOnTargetState.none -> Unit
                }
            }
        })
    }

    private fun successCreatingTarget() {
        expandCollapseCreateTargetSheet()
        hideKeyboard(bindingRoot.context, bindingRoot)
    }

    private fun showError(message: String) {
        DialogUtil.showError(bindingRoot.context, message)
    }

    companion object {
        const val SHOW_EMPTY_ERROR = " "
        const val DOUBLE_EMPTY_SPACE_FOR_SPAN = "  "
        const val MIN_CHAR_FILTER = 3
        const val TOPIC_SHEET_EXPANDED_RATIO = 0.70f
    }
}
