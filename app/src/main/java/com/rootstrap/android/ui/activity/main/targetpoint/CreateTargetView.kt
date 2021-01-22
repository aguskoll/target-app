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
    }

    private fun createTarget() {
        val title = binding.root.title_edit_text.value()
        val area = binding.root.area_edit_text.value().toDouble()
        val topic = binding.root.topic_edit_text.value().toInt()
        val lat = createTargetViewModel.getLocationLatitude()
        val lng = createTargetViewModel.getLocationLongitude()
        val target = Target(title, lat, lng, area, topic)

        if (createTargetViewModel.isLocationStateSuccess()) {
            createTargetViewModel.createTarget(target)
        }
    }

    fun expandCollapseSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}
