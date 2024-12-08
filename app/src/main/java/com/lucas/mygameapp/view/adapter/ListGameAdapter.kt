package com.lucas.mygameapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.lucas.mygameapp.R
import com.lucas.mygameapp.model.UserGame

private const val VIEW_TYPE_ITEM = 0

class ListGameAdapter (private val games: MutableList<UserGame>,
                       private val itemLayoutId: Int,
                       private val launcher : ActivityResultLauncher<Array<Pair<String, Int>>>,
                       private val viewAll : Boolean,
                       private val gameClickedFunction : (Int, UserGame) -> Unit,
                       private val checkLoadingFunction : (() -> Unit)? = null)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var context : Context? = null
    private var density : Float? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageHolder: ImageView
        val platform : TextView
        val cardView : CardView
        var rating : TextView

        init {
            imageHolder = view.findViewById(R.id.gameImageViewItemList)
            platform = view.findViewById(R.id.tvPlatformName)
            rating = view.findViewById(R.id.tvRating)
            cardView = view.findViewById(R.id.cvCoverImage)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
        density = recyclerView.context.resources.displayMetrics.density
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(itemLayoutId, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (checkLoadingFunction != null && position == (games.size - 1)) {
            checkLoadingFunction.invoke()
        }
        return VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder is ViewHolder) {

            if (!viewAll && position + 1 == games.size) {
                val paddingRight = 10

                val param = viewHolder.cardView.layoutParams as ViewGroup.MarginLayoutParams
                param.marginEnd = (density!! * paddingRight).toInt()
                viewHolder.cardView.layoutParams = param
            }

            if (games[position].game?.coverBitmap != null) {
                viewHolder.imageHolder.setImageBitmap(games[position].game?.coverBitmap!!)
            }

            viewHolder.platform.text =
                if (games[position].platform == "Genesis/MegaDrive") "MegaDrive" else games[position].platform
            viewHolder.platform.visibility =
                if (games[position].platform != null) View.VISIBLE else View.GONE

            if (games[position].rating != null) {
                val intRating = games[position].rating!!.toInt()

                viewHolder.rating.text =
                    if (games[position].rating!! - intRating == 0f) intRating.toString() else games[position].rating?.toString()
                viewHolder.rating.visibility = View.VISIBLE
            } else {
                viewHolder.rating.visibility = View.GONE
            }

            viewHolder.imageHolder.setOnClickListener {
                gameClickedFunction(viewHolder.adapterPosition, games[viewHolder.adapterPosition])

                val inputs = arrayOf(
                    Pair("gameId", games[position].gameId ?: 0),
                    Pair("userGameId", games[position].id ?: 0)
                )

                launcher.launch(inputs)
            }
        }
    }

    override fun getItemCount() = games.size
}