package com.lucas.mygameapp.view.gamedetail.bottomsheet

import android.app.Activity
import androidx.constraintlayout.widget.ConstraintLayout
import com.lucas.mygameapp.R
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity

class GameDetailOptionsBottomSheet(activity : Activity) : BottomSheet(activity, R.layout.bottom_sheet_game_detail_options) {

    init {
        dialog.findViewById<ConstraintLayout>(R.id.clPlatformOption).setOnClickListener {
            (activity as GameDetailActivity).openPlatformsBottomSheet()
        }

        dialog.findViewById<ConstraintLayout>(R.id.clPlayerDataOption).setOnClickListener {
            (activity as GameDetailActivity).openPlayerDataBottomSheet()
        }

        dialog.findViewById<ConstraintLayout>(R.id.clStatusOption).setOnClickListener {
            (activity as GameDetailActivity).openStatusBottomSheet()
        }

        dialog.findViewById<ConstraintLayout>(R.id.clRatingOption).setOnClickListener {
            (activity as GameDetailActivity).openRatingBottomSheet()
        }
    }
}