package com.lucas.mygameapp.view.gamedetail.bottomsheet

import android.app.Activity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.lucas.mygameapp.R
import com.lucas.mygameapp.databinding.SelectPlatformBinding
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity

class PlatformsBottomSheet(userGame : UserGame, activity : Activity) : BottomSheet(activity, R.layout.bottom_sheet_platforms) {

    private val UNSELECTED_TEXT_COLOR : Int
    private val SELECTED_TEXT_COLOR : Int

    private var selectedPlatform : String? = null

    init {
        UNSELECTED_TEXT_COLOR = ContextCompat.getColor(activity.applicationContext, R.color.black)
        SELECTED_TEXT_COLOR = ContextCompat.getColor(activity.applicationContext, R.color.white)

        for (platform in userGame.game?.platforms!!) {
            val binding = SelectPlatformBinding.inflate(activity.layoutInflater)
            binding.tvPlatformName.text = platform
            binding.llPlatformRoot.setOnClickListener {
                clickedPlatform(platform, binding.root)
            }

            if (userGame.platform == platform) {
                selectedPlatform = platform
                selectPlatform(binding.root)
            }

            dialog.findViewById<LinearLayout>(R.id.llPlatformsList).addView(binding.root)
        }

        dialog.findViewById<Button>(R.id.btnUpdatePlatform).setOnClickListener {
            (activity as GameDetailActivity).updateGamePlatform(selectedPlatform)
        }
    }

    private fun clickedPlatform(platform : String?, layout : LinearLayout) {

        val platformList = dialog.findViewById<LinearLayout>(R.id.llPlatformsList)

        for (platformLayout in platformList.children) {
            val platformRootLayout = platformLayout.findViewById<LinearLayout>(R.id.llPlatformRoot)
            unselectPlatform(platformRootLayout)
        }

        if (selectedPlatform != platform) {
            selectedPlatform = platform
            selectPlatform(layout)
        }
        else {
            selectedPlatform = null
        }
    }

    private fun selectPlatform(layout : LinearLayout) {
        layout.findViewById<LinearLayout>(R.id.llPlatformRoot).setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.selected_game_status))
        layout.findViewById<TextView>(R.id.tvPlatformName).setTextColor(SELECTED_TEXT_COLOR)
        layout.findViewById<ImageView>(R.id.imgPlatformIcon).setColorFilter(SELECTED_TEXT_COLOR)
    }

    private fun unselectPlatform(layout : LinearLayout) {
        layout.findViewById<LinearLayout>(R.id.llPlatformRoot).setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.unselected_game_status))
        layout.findViewById<TextView>(R.id.tvPlatformName).setTextColor(UNSELECTED_TEXT_COLOR)
        layout.findViewById<ImageView>(R.id.imgPlatformIcon).setColorFilter(UNSELECTED_TEXT_COLOR)
    }
}