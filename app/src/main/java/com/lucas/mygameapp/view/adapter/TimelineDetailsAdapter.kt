package com.lucas.mygameapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.mygameapp.R
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity
import com.lucas.mygameapp.view.timeline.TimelineDetailActivity
import java.text.SimpleDateFormat


private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy")

class TimelineDetailsAdapter (private val games: List<UserGame>, val activity : Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameImage : ImageView
        val platform : TextView
        val rating : TextView
        val startedAt : TextView
        val stoppedAt : TextView
        val time : TextView

        init {
            gameImage = view.findViewById(R.id.ivCoverImage)
            platform = view.findViewById(R.id.tvPlatformValue)
            rating = view.findViewById(R.id.tvRatingValue)
            startedAt = view.findViewById(R.id.tvStartedPlayedValue)
            stoppedAt = view.findViewById(R.id.tvStoppedPlayedValue)
            time = view.findViewById(R.id.tvTimeValue)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.timeline_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder is ViewHolder) {

            if (games[position].game?.coverBitmap != null) {
                viewHolder.gameImage.setImageBitmap(games[position].game?.coverBitmap!!)
            }

            viewHolder.platform.text = if (games[position].platform != null) games[position].platform else "-"
            viewHolder.time.text = if (games[position].playingTime != null) games[position].playingTime.toString() + "hs" else "-"
            viewHolder.startedAt.text = if(games[position].startPlayingOn != null) DATE_FORMAT.format(games[position].startPlayingOn!!) else "-"
            viewHolder.stoppedAt.text = if(games[position].stopPlayingOn != null) DATE_FORMAT.format(games[position].stopPlayingOn!!) else "-"
            viewHolder.stoppedAt.text = if(games[position].stopPlayingOn != null) DATE_FORMAT.format(games[position].stopPlayingOn!!) else "-"


            if (games[position].rating != null) {
                val intRating = games[position].rating!!.toInt()
                viewHolder.rating.text = if (games[position].rating!! - intRating == 0f) intRating.toString() else games[position].rating?.toString()
            }
            else {
                viewHolder.rating.text = "-"
            }

            viewHolder.gameImage.setOnClickListener {
                val intent : Intent = Intent(activity, GameDetailActivity::class.java)
                intent.putExtra("userGame", games[position])
                activity.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = games.size
}