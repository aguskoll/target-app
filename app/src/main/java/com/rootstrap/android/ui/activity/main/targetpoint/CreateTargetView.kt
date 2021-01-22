package com.rootstrap.android.ui.activity.main.targetpoint

import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rootstrap.android.databinding.ActivityTargetPointsBinding
import com.rootstrap.android.network.models.Target
import com.rootstrap.android.util.extensions.value
import kotlinx.android.synthetic.main.fragment_create_target.view.*

class CreateTargetView(
    private val binding: ActivityTargetPointsBinding,
    private val createTargetViewModel: CreateTargetViewModel
) {

    private val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> =
        BottomSheetBehavior.from<ConstraintLayout>(binding.root.create_target_bottom_sheet)

    init {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = TargetPointsActivity.PICK_HEIGHT_HIDDEN
        binding.root.save_target_btn.setOnClickListener {
            createTarget()
        }
        createTargetViewModel.getTopics()
    }

    // TODO: show real topics and handle selection
    private fun createTarget() {
        val title = binding.root.title_edit_text.value()
        val area: Double = binding.root.area_edit_text.value().toDoubleOrNull() ?: 0.0
        val topic: Int = binding.root.topic_edit_text.value().toIntOrNull() ?: 0
        val lat = createTargetViewModel.getLocationLatitude()
        val lng = createTargetViewModel.getLocationLongitude()
        val target = Target(title, lat, lng, area, topic)

        if (createTargetViewModel.isLocationStateSuccess() &&
            validateUserInputs(area, title, topic)
        ) {
            createTargetViewModel.createTarget(target)
        }
    }

    private fun validateUserInputs(area: Double, title: String?, topic: Int): Boolean =
        validateArea(area) && validateTargetTitle(title) && validateTopic(topic)

    private fun validateArea(area: Double): Boolean {
        val isAreaValid = createTargetViewModel.isAreaValid(area)
        if (isAreaValid.not())
            binding.root.area_text_input_layout.error = " "
        return isAreaValid
    }

    private fun validateTargetTitle(title: String?): Boolean {
        val isTitleValid = createTargetViewModel.isTitleValid(title)
        if (isTitleValid.not()) {
            binding.root.title_text_input_layout.error = " "
        }
        return isTitleValid
    }

    private fun validateTopic(topic: Int): Boolean {
        val isTopicValid = createTargetViewModel.isTopicValid(topic)
        if (isTopicValid) {
            binding.root.topic_text_input_layout.error = ""
        }
        return isTopicValid
    }

    fun expandCollapseSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}
