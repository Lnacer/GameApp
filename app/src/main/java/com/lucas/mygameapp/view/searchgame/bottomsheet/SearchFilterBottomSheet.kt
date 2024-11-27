package com.lucas.mygameapp.view.searchgame.bottomsheet

import android.app.Activity
import android.graphics.Rect
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lucas.mygameapp.Integration.Supabase.PlatformIntegration
import com.lucas.mygameapp.R
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.model.Platform
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity
import kotlin.concurrent.thread

class SearchFilterBottomSheet(activity : Activity, private val onFilterApplied : (List<Platform>, Boolean) -> Unit) : BottomSheet(activity, R.layout.bottom_sheet_search_filter) {

    private val platformBottomSheet : SearchFilterOptionsBottomSheet
    private lateinit var platforms : List<Platform>
    private var selectedPlatforms : List<Platform> = mutableListOf()
    private var bottomWindowSize = 0
    private var isKeyboardOpen = false
    private var keyboardSize = 0

    init {

        platformBottomSheet = SearchFilterOptionsBottomSheet(activity, emptyList(), "Platforms", ::onPlatformSelected)

        dialog.findViewById<TextView>(R.id.txtPlatformsSelect).setOnClickListener {
            platformBottomSheet.show()
        }

        dialog.findViewById<Button>(R.id.btnSearchFilter).setOnClickListener {
            onFilterApplied(selectedPlatforms, true)
        }

        dialog.findViewById<TextView>(R.id.txtClearFilter).setOnClickListener {
            clearFilter()
        }

        thread {
            val database =  Database.getInstance(activity)

            platforms = PlatformIntegration.getAllPlatforms()

            activity.runOnUiThread {
                platformBottomSheet.updateList(platforms.map { it.name!! })
            }
        }

        setKeyboardViewChangedListener()
    }

    private fun setKeyboardViewChangedListener() {
        activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)

            if (rect.bottom >= bottomWindowSize) {
                bottomWindowSize = rect.bottom

                if (isKeyboardOpen) {
                    platformBottomSheet.keyboardVisibilityChange(false, 0)
                }

                isKeyboardOpen = false
            }
            else {
                if (!isKeyboardOpen) {
                    keyboardSize = bottomWindowSize - rect.bottom

                    platformBottomSheet.keyboardVisibilityChange(true, keyboardSize)
                }

                isKeyboardOpen = true
            }
        }
    }

    private fun onPlatformSelected(appliedPlatforms : List<String>) {
        selectedPlatforms = platforms.filter {  appliedPlatforms.contains(it.name)  }

        dialog.findViewById<TextView>(R.id.txtPlatformsAll).text = if (selectedPlatforms.isEmpty()) "All" else selectedPlatforms.size.toString()

        platformBottomSheet.dialog.dismiss()

        onFilterApplied(selectedPlatforms, false)
    }

    fun clearFilter() {
        selectedPlatforms = mutableListOf()
        platformBottomSheet.updateSelectedOptions(emptyList())

        dialog.findViewById<TextView>(R.id.txtPlatformsAll).text = "All"

        onFilterApplied(selectedPlatforms, false)
    }
}