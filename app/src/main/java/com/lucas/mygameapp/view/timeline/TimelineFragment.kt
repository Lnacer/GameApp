package com.lucas.mygameapp.view.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
            val database = Database.getInstance(activity?.applicationContext!!)
            val playedGames = database.userGameDao().getAllPlayaedWithDate()
            val beatenGames = database.userGameDao().getAllBeatenWithDate()

            val timelineMap = mutableMapOf<Int, Timeline>()

            for (userGame in playedGames) {

                if (userGame.startPlayingOn != null) {

                    val startPlayingDate = GregorianCalendar()
                    startPlayingDate.time = userGame.startPlayingOn!!
                    val year = startPlayingDate.get(Calendar.YEAR)

                    if (!timelineMap.containsKey(year)) {
                        timelineMap[year] = Timeline(year)
                    }

                    timelineMap[year]!!.playedGamesCount = timelineMap[year]!!.playedGamesCount!! + 1
                }
            }

            for (userGame in beatenGames) {

                if (userGame.stopPlayingOn != null) {

                    val startPlayingDate = GregorianCalendar()
                    startPlayingDate.time = userGame.stopPlayingOn!!
                    val year = startPlayingDate.get(Calendar.YEAR)

                    if (!timelineMap.containsKey(year)) {
                        timelineMap[year] = Timeline(year)
                    }

                    timelineMap[year]!!.beatenGamesCount = timelineMap[year]!!.beatenGamesCount!! + 1
                }
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