package com.lucas.mygameapp.view.gamedetail.bottomsheet

import android.annotation.SuppressLint
import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import com.lucas.mygameapp.R
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity

class GameRatingBottomSheet(var currentRating : Float?, activity : Activity) : BottomSheet(activity, R.layout.bottom_sheet_game_rate) {

    var ratingChanged : Boolean = false

    init {
        val ratingView = dialog.findViewById<RatingBar>(R.id.rbRateGame)
        ratingView.rating = currentRating ?: 0f

        ratingView.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (rating == 0f) {
                currentRating = null
            }
            else {
                currentRating = rating
                ratingChanged = true
            }
        }

        ratingView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {

                val response = v.onTouchEvent(event)

                if (event?.action == MotionEvent.ACTION_UP) {
                    if (ratingView.rating == currentRating && !ratingChanged) {
                        ratingView.rating = 0f
                    }
                }

                ratingChanged = false

                return response
            }
        })


        dialog.findViewById<Button>(R.id.btnUpdateGameRating).setOnClickListener {
            (activity as GameDetailActivity).updateGameRating(currentRating)
        }
    }
}