package com.lucas.mygameapp.view.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucas.mygameapp.Integration.Supabase.UserGameIntegration
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.databinding.FragmentProfileBinding
import com.lucas.mygameapp.databinding.FragmentTimelineBinding
import com.lucas.mygameapp.model.GameStatus
import com.lucas.mygameapp.model.Timeline
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.utils.BitmapUtil
import com.lucas.mygameapp.view.MainActivity
import com.lucas.mygameapp.view.adapter.SearchGameAdapter
import com.lucas.mygameapp.view.adapter.TimelineAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.concurrent.thread

class TimelineFragment : Fragment() {

    private lateinit var binding : FragmentTimelineBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentTimelineBinding.inflate(inflater, container, false)

        thread {

            val beatenGames = UserGameIntegration.getTimelineGamesCount(GameStatus.FINISHED)
            val playedGames = UserGameIntegration.getTimelineGamesCount(GameStatus.PLAYING)

            val timelineMap = mutableMapOf<Int, Timeline>()

            for (userGame in beatenGames) {

                val year = userGame.year

                if (!timelineMap.containsKey(year)) {
                    timelineMap[year] = Timeline(year)
                }

                timelineMap[year]!!.beatenGamesCount = userGame.count
            }

            for (userGame in playedGames) {

                val year = userGame.year

                if (!timelineMap.containsKey(year)) {
                    timelineMap[year] = Timeline(year)
                }

                timelineMap[year]!!.playedGamesCount = userGame.count
            }

            val sortedMap = timelineMap.toSortedMap(reverseOrder())

            requireActivity().runOnUiThread {

                val listGameAdapter = TimelineAdapter(sortedMap.values.toList(), requireActivity())

                binding.rvTimeline.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.rvTimeline.adapter = listGameAdapter
            }
        }

        return binding.root
    }
}