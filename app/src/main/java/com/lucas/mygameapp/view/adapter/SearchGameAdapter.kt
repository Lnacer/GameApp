package com.lucas.mygameapp.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.mygameapp.Integration.IgdbIntegration
import com.lucas.mygameapp.R
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity
import java.text.SimpleDateFormat

private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy")
private const val VIEW_TYPE_ITEM = 0
private const val VIEW_TYPE_LOADING = 1

class SearchGameAdapter (private val games: List<Game?>, val activity : Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var context : Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameName: TextView
        val image : ImageView
        val releaseDate : TextView
        val publisherName : TextView
        val parentLayout : LinearLayout

        init {
            parentLayout = view.findViewById(R.id.llGameSearchItem)
            gameName = view.findViewById(R.id.tvGameSearchName)
            image = view.findViewById(R.id.imgGameSearchImage)
            releaseDate = view.findViewById(R.id.tvReleaseDate)
            publisherName = view.findViewById(R.id.tvPublisherName)
        }
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progressBar : ProgressBar

        init {
            progressBar = view.findViewById(R.id.pbLoading)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.game_search_item, viewGroup, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_loading, viewGroup, false)
            return LoadingViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (games[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder is ViewHolder) {
            viewHolder.parentLayout.setOnClickListener {
                val intent = Intent(context, GameDetailActivity::class.java)
                intent.putExtra("gameId", games[position]?.id!!)
                context?.startActivity(intent)
            }

            viewHolder.gameName.text = games[position]?.name
            viewHolder.releaseDate.text = ""
            if (games[position]?.releaseDate != null) {
                viewHolder.releaseDate.text = DATE_FORMAT.format(games[position]?.releaseDate!!)
            }

            viewHolder.publisherName.text = ""
            if (games[position]?.publishers != null && games[position]?.publishers!!.isNotEmpty()) {
                viewHolder.publisherName.text = games[position]?.publishers!![0]
            }

            if (games[position]?.coverBitmap != null) {
                viewHolder.image.setImageBitmap(games[position]?.coverBitmap)
            }
            else if (games[position]?.coverUrl != null) {

                Thread {
                    val coverBitmap = IgdbIntegration.getImageBitmap(games[position]?.coverUrl!!)

                    if (coverBitmap != null) {
                        activity.runOnUiThread {
                            kotlin.run {
                                viewHolder.image.setImageBitmap(coverBitmap)
                            }
                        }
                    }
                }.start()
            }
        }

    }

    override fun getItemCount() = games.size
}