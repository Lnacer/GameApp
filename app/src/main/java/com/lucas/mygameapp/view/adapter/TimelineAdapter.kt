package com.lucas.mygameapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.mygameapp.R
import com.lucas.mygameapp.model.Timeline
import com.lucas.mygameapp.view.MainActivity
import com.lucas.mygameapp.view.timeline.TimelineDetailActivity

class TimelineAdapter (private val timeline: List<Timeline>, val activity : Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val year: TextView
        val playedGames : TextView
        val beatenGames : TextView
        val viewDetail : TextView

        init {
            year = view.findViewById(R.id.tvYearLabel)
            playedGames = view.findViewById(R.id.tvGamesPlayedCount)
            beatenGames = view.findViewById(R.id.tvGamesBeatenCount)
            viewDetail = view.findViewById(R.id.tvViewDetails)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.yearly_timeline_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder is ViewHolder) {
            viewHolder.year.text = timeline[position].year.toString()
            viewHolder.playedGames.text = if (timeline[position].playedGamesCount != null) timeline[position].playedGamesCount.toString() else "0"
            viewHolder.beatenGames.text = if (timeline[position].beatenGamesCount != null) timeline[position].beatenGamesCount.toString() else "0"

            viewHolder.viewDetail.setOnClickListener {
                val intent = Intent(activity, TimelineDetailActivity::class.java)
                intent.putExtra("year", timeline[position].year)
                activity.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = timeline.size
}