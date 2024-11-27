package com.lucas.mygameapp.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.lucas.mygameapp.R
import com.lucas.mygameapp.model.UserGame
import java.text.SimpleDateFormat

class ScreenshotsAdapter(private val screenshots: List<Bitmap?>, private val onClickFunction : (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var density : Float? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val screenshot : ShapeableImageView

        init {
            screenshot = view.findViewById(R.id.imgGameScreenshot)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        density = recyclerView.context.resources.displayMetrics.density
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.game_screenshot_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ViewHolder) {

            if (position + 1 == screenshots.size) {
                val paddingRight = 15

                val param = viewHolder.screenshot.layoutParams as ViewGroup.MarginLayoutParams
                param.marginEnd = (density!! * paddingRight).toInt()
                viewHolder.screenshot.layoutParams = param
            }

            viewHolder.screenshot.setImageBitmap(screenshots[position])

            viewHolder.screenshot.setOnClickListener {
                onClickFunction(position)
            }
        }
    }

    override fun getItemCount() = screenshots.size
}