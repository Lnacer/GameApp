package com.lucas.mygameapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.mygameapp.R

class SearchOptionAdapter(private var options: List<String>, private val onClickFunction : (String, Boolean) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var selectedOptions : MutableList<String> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val optionText : TextView
        val selectedImage : ImageView

        init {
            optionText = view.findViewById(R.id.txtOptionText)
            selectedImage = view.findViewById(R.id.imgOptionSelected)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_option_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ViewHolder) {

            viewHolder.optionText.text = options[position]

            if (selectedOptions.contains(options[position])) {
                viewHolder.selectedImage.visibility = View.VISIBLE
            }
            else {
                viewHolder.selectedImage.visibility = View.INVISIBLE
            }

            viewHolder.optionText.setOnClickListener {

                val selected = selectedOptions.contains(options[position])
                if (selected) {
                    selectedOptions.remove(options[position])
                    viewHolder.selectedImage.visibility = View.INVISIBLE
                }
                else {
                    selectedOptions.add(options[position])
                    viewHolder.selectedImage.visibility = View.VISIBLE
                }

                onClickFunction(options[position], !selected)
            }
        }
    }

    override fun getItemCount() = options.size

    fun updateOptions(newOptions : List<String>) {
        options = newOptions
        notifyDataSetChanged()
    }

    fun updatedSelectedOptions(newSelectedOptions : List<String>) {
        selectedOptions = newSelectedOptions.toMutableList()
        notifyDataSetChanged()
    }
}