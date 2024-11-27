package com.lucas.mygameapp.view.timeline

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucas.mygameapp.R
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.databinding.ActivityMainBinding
import com.lucas.mygameapp.databinding.ActivityTimelineDetailBinding
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.view.adapter.TimelineAdapter
import com.lucas.mygameapp.view.adapter.TimelineDetailsAdapter
import java.util.Date
import java.util.GregorianCalendar
import kotlin.concurrent.thread

class TimelineDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimelineDetailBinding
    private var year : Int = 0
    private var orderByStartedDate = true

    private lateinit var selectedColor : ColorStateList
    private lateinit var unselectedColor : ColorStateList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimelineDetailBinding.inflate(layoutInflater)

        selectedColor = resources.getColorStateList(R.color.colorPrimary, theme)
        unselectedColor = resources.getColorStateList(R.color.grey, theme)

        year = intent.getIntExtra("year", 0)

        binding.tvTimelineYearTitle.text = year.toString()

        binding.rvTimelineDetail.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvOrderByStartDate.setOnClickListener {
            if (!orderByStartedDate) {
                binding.tvOrderByStartDate.backgroundTintList = selectedColor
                binding.tvOrderByStopDate.backgroundTintList = unselectedColor

                orderByStartedDate = true

                updateGames(true)
            }
        }

        binding.tvOrderByStopDate.setOnClickListener {
            if (orderByStartedDate) {
                binding.tvOrderByStopDate.backgroundTintList = selectedColor
                binding.tvOrderByStartDate.backgroundTintList = unselectedColor

                orderByStartedDate = false

                updateGames(false)
            }
        }

        updateGames(orderByStartedDate)

        setContentView(binding.root)
    }

    private fun updateGames(orderByStartedDate : Boolean) {
        thread {
            val database = Database.getInstance(applicationContext)

            val startDate = GregorianCalendar(year, 0, 1).time
            val endDate = GregorianCalendar(year, 11, 31).time

            val playedGames = if (orderByStartedDate) {
                database.userGameDao().getAllPlayedBetweenDates(startDate, endDate)
            } else {
                database.userGameDao().getAllBeatenBetweenDates(startDate, endDate)
            }

            runOnUiThread {
                val listGameAdapter = TimelineDetailsAdapter(playedGames, this)
                binding.rvTimelineDetail.adapter = listGameAdapter
                binding.tvTimelineYearTitle.text = year.toString() + " (" + playedGames.size.toString() +  ")"
            }
        }
    }
}