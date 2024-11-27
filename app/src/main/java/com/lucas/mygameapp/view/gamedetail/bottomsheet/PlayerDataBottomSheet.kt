package com.lucas.mygameapp.view.gamedetail.bottomsheet

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.lucas.mygameapp.Integration.Supabase.UserGameIntegration
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.R
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.utils.MaskWatcher
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity
import com.lucas.mygameapp.view.utils.DatePickerFragment
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.concurrent.thread
import kotlin.reflect.KType

class PlayerDataBottomSheet(private val userGame : UserGame, activity : AppCompatActivity) : BottomSheet(activity, R.layout.bottom_sheet_player_data) {

    private val formatter : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    init {
        val startedOn = dialog.findViewById<TextInputEditText>(R.id.txtStartedOn)
        startedOn.text?.append(if(userGame.startPlayingOn != null) formatter.format(userGame.startPlayingOn!!) else "")
        startedOn.addTextChangedListener(MaskWatcher("##/##/####"))

        val finishedOn = dialog.findViewById<TextInputEditText>(R.id.txtFinishedOn)
        finishedOn.text?.append(if(userGame.stopPlayingOn != null) formatter.format(userGame.stopPlayingOn!!) else "")
        finishedOn.addTextChangedListener(MaskWatcher("##/##/####"))

        val btnStartedOn = dialog.findViewById<ImageButton>(R.id.btnStartedOn)
        btnStartedOn.setOnClickListener {

            val newFragment = DatePickerFragment(getDate(startedOn)) {
                startedOn.text?.clear()
                startedOn.text?.append(formatter.format(it))
            }

            newFragment.show(activity.supportFragmentManager, "datePicker")
        }

        val btnFinishedOn = dialog.findViewById<ImageButton>(R.id.btnFinishedOn)
        btnFinishedOn.setOnClickListener {

            val newFragment = DatePickerFragment(getDate(finishedOn)) {
                finishedOn.text?.clear()
                finishedOn.text?.append(formatter.format(it))
            }

            newFragment.show(activity.supportFragmentManager, "datePicker")
        }

        val playTime = dialog.findViewById<TextInputEditText>(R.id.txtPlayTime)
        playTime.text?.append(if(userGame.playingTime != null) userGame.playingTime.toString() else "")

        val progress = dialog.findViewById<TextInputEditText>(R.id.txtProgress)

        progress.addTextChangedListener {
            try {
                if (!isBlank(progress.text?.toString())) {
                    if (progress.text.toString().toInt() > 100) {
                        progress.text?.clear()
                        progress.text?.append("100")
                    }
                }
            }
            catch (ex : Exception) {
                progress.text?.clear()
            }
        }

        progress.text?.append(if(userGame.progress != null) userGame.progress.toString() else "")

        dialog.findViewById<Button>(R.id.btnUpdateGameInfo).setOnClickListener {

            var startedOnDate : Date? = userGame.startPlayingOn
            var finishedOnDate : Date? = userGame.stopPlayingOn
            var error : String? = null

            try {
                startedOnDate = getDate(startedOn)
            }
            catch (ex : Exception) {
                error = "Invalid date"
            }

            try {
                finishedOnDate = getDate(finishedOn)
            }
            catch (ex : Exception) {
                error = "Invalid date"
            }

            if (error == null) {
                userGame.startPlayingOn = startedOnDate
                userGame.stopPlayingOn = finishedOnDate

                if (!isBlank(playTime.text?.toString())) {
                    userGame.playingTime = playTime.text.toString().toInt()
                }
                else {
                    userGame.playingTime = null
                }

                if (!isBlank(progress.text?.toString())) {
                    userGame.progress = progress.text.toString().toInt()
                }
                else {
                    userGame.progress = null
                }

                thread {
                    UserGameIntegration.upsertUserGame(userGame)
                }

                (activity as GameDetailActivity).updateUserDetailViews()
                dialog.dismiss()
            }
            else {
                Toast.makeText(dialog.context,error,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun keyboardVisibilityChange(isOpen : Boolean, keyboardHeigh : Int?) {
        if (keyboardHeigh != null) {
            val layout = dialog.findViewById<LinearLayout>(R.id.llUndateInfoBottomSheet)
            val params = layout.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, keyboardHeigh)

            layout.layoutParams = params
        }
    }

    private fun isBlank(text : String?) : Boolean {
        return text == null || text.trim() == ""
    }

    private fun getDate(inputText : TextInputEditText) : Date? {
        if (!isBlank(inputText.text?.toString())) {
            return formatter.parse(inputText.text.toString())
        }

        return null
    }
}