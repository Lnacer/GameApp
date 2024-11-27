package com.lucas.mygameapp.view.allgames.bottomsheet

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.textfield.TextInputEditText
import com.lucas.mygameapp.R
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.databinding.PlatformTagBinding
import com.lucas.mygameapp.model.Platform
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity
import com.lucas.mygameapp.view.searchgame.bottomsheet.SearchFilterOptionsBottomSheet
import kotlin.concurrent.thread

class SearchGamesBottomSheet(activity : Activity, games : List<UserGame>, private val onFilterApplied : (List<String>, String?) -> Unit) : BottomSheet(activity, R.layout.bottom_sheet_search_games) {

    private var platforms : MutableList<String> = mutableListOf()
    private var platformsSelected : MutableList<String> = mutableListOf()
    private var bottomWindowSize = 0
    private var isKeyboardOpen = false
    private var keyboardSize = 0

    init {

        val fbPlatformLayout = dialog.findViewById<FlexboxLayout>(R.id.fbPlataforms)

        for (userGame in games) {
            if (userGame.platform != null && !platforms.contains(userGame.platform)) {
                platforms.add(userGame.platform!!)

                val tagBinding = PlatformTagBinding.inflate(activity.layoutInflater)
                tagBinding.tvPlatformName.text = userGame.platform!!

                tagBinding.tvPlatformName.setPadding(20, 15, 20, 15)

                tagBinding.llPlatformTag.setOnClickListener {

                    if (platformsSelected.contains(userGame.platform!!)) {
                        tagBinding.tvPlatformName.background = ContextCompat.getDrawable(activity.applicationContext, R.drawable.tag)
                        tagBinding.tvPlatformName.setTextColor(ContextCompat.getColor(activity.applicationContext, R.color.black))
                        platformsSelected.remove(userGame.platform!!)
                    }
                    else {
                        tagBinding.tvPlatformName.background = ContextCompat.getDrawable(activity.applicationContext, R.drawable.tag_selected)
                        tagBinding.tvPlatformName.setTextColor(ContextCompat.getColor(activity.applicationContext, R.color.white))
                        platformsSelected.add(userGame.platform!!)
                    }
                }

                fbPlatformLayout.addView(tagBinding.root)
            }
        }

        dialog.findViewById<Button>(R.id.btnApplyFilter).setOnClickListener {
            onFilterApplied(platformsSelected, dialog.findViewById<TextInputEditText>(R.id.txtSearch).text?.toString())
        }

        setKeyboardViewChangedListener()
    }

    override fun keyboardVisibilityChange(isOpen : Boolean, keyboardHeigh : Int?) {
        if (keyboardHeigh != null) {
            val layout = dialog.findViewById<LinearLayout>(R.id.rootLayout)
            val params = layout.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, keyboardHeigh)

            layout.layoutParams = params
        }
    }

    private fun setKeyboardViewChangedListener() {
        activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)

            if (rect.bottom >= bottomWindowSize) {
                bottomWindowSize = rect.bottom

                if (isKeyboardOpen) {
                    keyboardVisibilityChange(false, 0)
                }

                isKeyboardOpen = false
            }
            else {
                if (!isKeyboardOpen) {
                    keyboardSize = bottomWindowSize - rect.bottom

                    keyboardVisibilityChange(true, keyboardSize)
                }

                isKeyboardOpen = true
            }
        }
    }
}