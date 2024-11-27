package com.lucas.mygameapp.view.searchgame.bottomsheet

import android.app.Activity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.lucas.mygameapp.R
import com.lucas.mygameapp.view.adapter.SearchOptionAdapter
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.view.gamedetail.GameDetailActivity

class SearchFilterOptionsBottomSheet(activity : Activity, private var options : List<String>, title : String, private val onApllyFilter : (List<String>) -> Unit) : BottomSheet(activity, R.layout.bottom_sheet_search_filter_options) {

    private var adapter : SearchOptionAdapter
    private var selectedValue : List<String> = mutableListOf()
    private var notAppliedOptions : MutableList<String> = mutableListOf()
    private var visibleOptions = options.toMutableList()

    init {
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvFilterOptions)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = SearchOptionAdapter(options, ::onOptionClicked)
        recyclerView.adapter = adapter

        dialog.findViewById<TextView>(R.id.txtFilterOptionTitle).text = title

        dialog.findViewById<TextInputEditText>(R.id.txtSearchOptions).doOnTextChanged { text, _, _, _ ->

            if (text != null && text.trim() != "") {
                val textTrim = text.trim()
                visibleOptions = options.filter { it.contains(textTrim, true) }.toMutableList()
            }
            else {
                visibleOptions = options.toMutableList()
            }

            adapter.updateOptions(visibleOptions)
        }

        dialog.findViewById<Button>(R.id.btnApplyFilter).setOnClickListener {
            selectedValue = notAppliedOptions.toMutableList()
            onApllyFilter(selectedValue)
        }
    }

    override fun show() {
        notAppliedOptions = selectedValue.toMutableList()
        adapter.updatedSelectedOptions(selectedValue)
        super.show()
    }

    override fun keyboardVisibilityChange(isOpen : Boolean, keyboardHeigh : Int?) {
        if (keyboardHeigh != null) {
            val layout = dialog.findViewById<ViewGroup>(R.id.rootLayout)
            val params = layout.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, keyboardHeigh)

            layout.layoutParams = params
        }
    }

    private fun onOptionClicked(value : String, selected: Boolean) {
        if (selected) {
            notAppliedOptions.add(value)
        }
        else {
            notAppliedOptions.remove(value)
        }
    }

    fun updateList(newOptions : List<String>) {
        adapter.updateOptions(newOptions)
        options = newOptions
        visibleOptions = options.toMutableList()
    }

    fun updateSelectedOptions(newSelectedOptions : List<String>) {
        selectedValue = newSelectedOptions.toMutableList()
    }
}