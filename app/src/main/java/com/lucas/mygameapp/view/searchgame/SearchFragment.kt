package com.lucas.mygameapp.view.searchgame

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.lucas.mygameapp.Integration.IgdbIntegration
import com.lucas.mygameapp.R
import com.lucas.mygameapp.databinding.FragmentProfileBinding
import com.lucas.mygameapp.databinding.FragmentSearchBinding
import com.lucas.mygameapp.view.adapter.SearchGameAdapter
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.Platform
import com.lucas.mygameapp.view.MainActivity
import com.lucas.mygameapp.view.searchgame.bottomsheet.SearchFilterBottomSheet

class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding
    private val games : MutableList<Game?> = mutableListOf()
    private var loadingGames : Boolean = false
    private var searchTerm : String = ""
    private var platformsFiltered : List<Platform> = emptyList()
    private lateinit var filterBottomSheet : SearchFilterBottomSheet

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        filterBottomSheet = SearchFilterBottomSheet(requireActivity(), ::onFilterApplied)

        binding.btnCleanSearchText.setOnClickListener {
            displayKeyboard(binding.txtSearch)
            binding.txtSearch.text?.clear()
            binding.txtSearch.requestFocus()
        }

        binding.btnSearchFilter.setOnClickListener {
            filterBottomSheet.show()
        }

        binding.btnClearFilter.setOnClickListener {
            filterBottomSheet.clearFilter()
        }

        binding.txtSearch.addTextChangedListener {
            if (binding.txtSearch.text != null && binding.txtSearch.text!!.isNotEmpty()) {
                binding.btnCleanSearchText.visibility = View.VISIBLE
            }
            else {
                binding.btnCleanSearchText.visibility = View.INVISIBLE
            }
        }

        binding.txtSearch.setOnKeyListener { _, keyCode, event ->
            when {
                (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) -> {
                    if (binding.txtSearch.text != null) {
                        getListGames(true).start()
                    }
                    return@setOnKeyListener true
                }
                else -> false
            }
        }

        binding.btnSearch.setOnClickListener {

            if (binding.txtSearch.text != null && binding.txtSearch.text.toString().trim() != "") {
                getListGames(true).start()
            }
        }

        binding.rvSearchGames.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager;

                if (!loadingGames) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == games.size - 2) {
                        getListGames(false).start()
                    }
                }
            }
        })

        return binding.root
    }

    private fun getListGames(firstSearch : Boolean): Thread {
        val recyclerView = binding.rvSearchGames
        val progressbar = binding.pbSearchLoading

        hideKeyboard()

        if (firstSearch) {
            searchTerm = binding.txtSearch.text.toString().trim()
            progressbar.visibility = View.VISIBLE
            games.clear()
        }
        else {
            games.add(null)
            recyclerView.adapter?.notifyItemInserted(games.size - 1)
        }

        binding.tvEmptySearchList.visibility = View.GONE
        binding.imgEmptySearchList.visibility = View.GONE

        loadingGames = true;

        return Thread {

            val recentlyGames = IgdbIntegration.getListOfGames(searchTerm, platformsFiltered, games.size, MainActivity.loggedUser)

            if (!firstSearch) {
                games.removeLast()
            }

            val indexRemoved = games.size

            games.addAll(recentlyGames)

            loadingGames = false
            activity?.runOnUiThread {
                kotlin.run {
                    if (games.isEmpty()) {
                        binding.tvEmptySearchList.visibility = View.VISIBLE
                        binding.imgEmptySearchList.visibility = View.VISIBLE
                        binding.tvEmptySearchList.text = "No games found :("
                    }

                    val listGameAdapter = SearchGameAdapter(games, requireActivity())

                    if (firstSearch) {
                        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        recyclerView.adapter = listGameAdapter
                    }
                    else {
                        recyclerView.adapter?.notifyItemRemoved(indexRemoved)
                        recyclerView.adapter?.notifyItemRangeInserted(indexRemoved, recentlyGames.size)
                    }

                    progressbar.visibility = View.GONE
                }
            }
        }
    }

    private fun onFilterApplied(platforms : List<Platform>, filter : Boolean) {
        platformsFiltered = platforms

        binding.txtFilterApplied.text = "Filtered by"
        if (platformsFiltered.isNotEmpty()) {
            binding.vFilterApplied.visibility = View.VISIBLE
            binding.txtFilterApplied.text = "${binding.txtFilterApplied.text} platform"
        }
        else {
            binding.vFilterApplied.visibility = View.GONE
        }

        if (filter) {
            filterBottomSheet.dialog.dismiss()
            getListGames(true).start()
        }
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun displayKeyboard(editText : View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}