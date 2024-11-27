package com.lucas.mygameapp.view.gamedetail.bottomsheet

import android.app.Activity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.R
import com.lucas.mygameapp.model.GameStatus
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity

class GameStatusBottomSheet(private var statusSelected : GameStatus?, activity : Activity) : BottomSheet(activity, R.layout.bottom_sheet_game_status) {
    private val UNSELECTED_TEXT_COLOR : Int
    private val SELECTED_TEXT_COLOR : Int

    init {
        UNSELECTED_TEXT_COLOR = ContextCompat.getColor(activity.applicationContext, R.color.black)
        SELECTED_TEXT_COLOR = ContextCompat.getColor(activity.applicationContext, R.color.white)

        val wantView = dialog.findViewById<LinearLayout>(R.id.layoutWantOption)
        val playingView = dialog.findViewById<LinearLayout>(R.id.layoutPlayingOption)
        val beatenView = dialog.findViewById<LinearLayout>(R.id.layoutBeatenOption)
        val pausedView = dialog.findViewById<LinearLayout>(R.id.layoutPausedOption)
        val stopedView = dialog.findViewById<LinearLayout>(R.id.layoutStopedOption)

        var viewSelected = updateSelectedView()

        wantView.setOnClickListener {
            if (statusSelected == GameStatus.WANT) {
                clearSelection()
                viewSelected = null
            }
            else {
                selectView(GameStatus.WANT, viewSelected, wantView)
                viewSelected = wantView
            }
        }

        playingView.setOnClickListener {
            if (statusSelected == GameStatus.PLAYING) {
                clearSelection()
                viewSelected = null
            }
            else {
                selectView(GameStatus.PLAYING, viewSelected, playingView)
                viewSelected = playingView
            }
        }

        beatenView.setOnClickListener {
            if (statusSelected == GameStatus.FINISHED) {
                clearSelection()
                viewSelected = null
            }
            else {
                selectView(GameStatus.FINISHED, viewSelected, beatenView)
                viewSelected = beatenView
            }
        }

        pausedView.setOnClickListener {
            if (statusSelected == GameStatus.PAUSED) {
                clearSelection()
                viewSelected = null
            }
            else {
                selectView(GameStatus.PAUSED, viewSelected, pausedView)
                viewSelected = pausedView
            }
        }

        stopedView.setOnClickListener {
            if (statusSelected == GameStatus.STOPPED) {
                clearSelection()
                viewSelected = null
            }
            else {
                selectView(GameStatus.STOPPED, viewSelected, stopedView)
                viewSelected = stopedView
            }
        }

        dialog.findViewById<Button>(R.id.btnUpdateStatus).setOnClickListener {
            (activity as GameDetailActivity).updateGameStatus(statusSelected)
            dialog.dismiss()
        }
    }

    fun updateSelectedView() : LinearLayout? {
        var viewSelected : LinearLayout? = null

        val wantView = dialog.findViewById<LinearLayout>(R.id.layoutWantOption)
        val playingView = dialog.findViewById<LinearLayout>(R.id.layoutPlayingOption)
        val beatenView = dialog.findViewById<LinearLayout>(R.id.layoutBeatenOption)
        val pausedView = dialog.findViewById<LinearLayout>(R.id.layoutPausedOption)
        val stopedView = dialog.findViewById<LinearLayout>(R.id.layoutStopedOption)

        if (statusSelected != null) {
            when (statusSelected) {
                GameStatus.WANT -> {
                    viewSelected = wantView
                }
                GameStatus.PLAYING -> {
                    viewSelected = playingView
                }
                GameStatus.FINISHED -> {
                    viewSelected = beatenView
                }
                GameStatus.PAUSED -> {
                    viewSelected = pausedView
                }
                GameStatus.STOPPED -> {
                    viewSelected = stopedView
                }
                else -> {}
            }

            selectView(statusSelected!!, null, viewSelected!!)
        }

        return viewSelected
    }

    fun selectView(statusSelected : GameStatus, oldLayout : LinearLayout?, newLayout : LinearLayout) {
        clearSelection()
        oldLayout?.setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.unselected_game_status))
        newLayout.setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.selected_game_status))

        this.statusSelected = statusSelected

        when (statusSelected) {
            GameStatus.WANT -> {
                dialog.findViewById<ImageView>(R.id.imgWantOption).setColorFilter(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvWantTitleOption).setTextColor(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvWantSubtitleOption).setTextColor(SELECTED_TEXT_COLOR)
            }
            GameStatus.PLAYING -> {
                dialog.findViewById<ImageView>(R.id.imgPlayingOption).setColorFilter(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvPlayingTitleOption).setTextColor(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvPlayingSubtitleOption).setTextColor(SELECTED_TEXT_COLOR)
            }
            GameStatus.FINISHED -> {
                dialog.findViewById<ImageView>(R.id.imgBeatenOption).setColorFilter(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvBeatenTitleOption).setTextColor(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvBeatenSubtitleOption).setTextColor(SELECTED_TEXT_COLOR)
            }
            GameStatus.STOPPED -> {
                dialog.findViewById<ImageView>(R.id.imgStopedOption).setColorFilter(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvStopedTitleOption).setTextColor(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvStopedSubtitleOption).setTextColor(SELECTED_TEXT_COLOR)
            }
            GameStatus.PAUSED -> {
                dialog.findViewById<ImageView>(R.id.imgPausedOption).setColorFilter(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvPausedTitleOption).setTextColor(SELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvPausedSubtitleOption).setTextColor(SELECTED_TEXT_COLOR)
            }
        }
    }

    private fun clearSelection() {

        when (statusSelected) {
            GameStatus.WANT -> {
                dialog.findViewById<ImageView>(R.id.imgWantOption).clearColorFilter()
                dialog.findViewById<TextView>(R.id.tvWantTitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvWantSubtitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<LinearLayout>(R.id.layoutWantOption).setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.unselected_game_status))
            }
            GameStatus.PLAYING -> {
                dialog.findViewById<ImageView>(R.id.imgPlayingOption).clearColorFilter()
                dialog.findViewById<TextView>(R.id.tvPlayingTitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvPlayingSubtitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<LinearLayout>(R.id.layoutPlayingOption).setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.unselected_game_status))
            }
            GameStatus.FINISHED -> {
                dialog.findViewById<ImageView>(R.id.imgBeatenOption).clearColorFilter()
                dialog.findViewById<TextView>(R.id.tvBeatenTitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvBeatenSubtitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<LinearLayout>(R.id.layoutBeatenOption).setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.unselected_game_status))
            }
            GameStatus.STOPPED -> {
                dialog.findViewById<ImageView>(R.id.imgStopedOption).clearColorFilter()
                dialog.findViewById<TextView>(R.id.tvStopedTitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvStopedSubtitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<LinearLayout>(R.id.layoutStopedOption).setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.unselected_game_status))
            }
            GameStatus.PAUSED -> {
                dialog.findViewById<ImageView>(R.id.imgPausedOption).clearColorFilter()
                dialog.findViewById<TextView>(R.id.tvPausedTitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<TextView>(R.id.tvPausedSubtitleOption).setTextColor(UNSELECTED_TEXT_COLOR)
                dialog.findViewById<LinearLayout>(R.id.layoutPausedOption).setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.unselected_game_status))
            }
            else -> {}
        }

        statusSelected = null
    }
}