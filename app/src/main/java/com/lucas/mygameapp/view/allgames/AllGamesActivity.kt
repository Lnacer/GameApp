package com.lucas.mygameapp.view.allgames

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import android.view.View
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import com.lucas.mygameapp.Integration.Supabase.UserGameIntegration
import com.lucas.mygameapp.view.adapter.ListGameAdapter
import com.lucas.mygameapp.R
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.databinding.ActivityAllGamesBinding
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.GameStatus
import com.lucas.mygameapp.model.Platform
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.view.allgames.bottomsheet.SearchGamesBottomSheet
import com.lucas.mygameapp.view.gamedetail.GameDetailActivityContract
import com.lucas.mygameapp.view.searchgame.bottomsheet.SearchFilterBottomSheet
import kotlin.concurrent.thread
import kotlin.math.ceil

class AllGamesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAllGamesBinding
    private lateinit var filterBottomSheet : SearchGamesBottomSheet
    private var selectedPlatform : List<String> = emptyList()
    private val userGames : MutableList<UserGame> = mutableListOf()
    private var userGamesFiltered : MutableList<UserGame> = mutableListOf()
    private var gameModified : Pair<Int, UserGame>? = null
    private var status : GameStatus? = null
    private var listGameAdapter : ListGameAdapter? = null
    private var sizeGames = 0
    private var loadedAll = false

    private val launcher = registerForActivityResult(GameDetailActivityContract()) {
        if (it != null && it.status != gameModified?.second?.status) {
            userGames.removeAt(gameModified?.first!!)
            userGamesFiltered.removeAt(gameModified?.first!!)
            binding.rvAllGames.adapter?.notifyItemRemoved(gameModified?.first!!)
            updateGamesCount()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAllGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val statusName = intent.getStringExtra("status")

        if (statusName != null) {
            status = GameStatus.byName(statusName)
        }

        binding.tvAllGamesTitle.text = statusName
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        updateNavigationBarHeight()

        thread {
            sizeGames = UserGameIntegration.getCounts(status!!)[status?.printableName!!] ?: 0
            userGames.addAll(UserGameIntegration.getUserGameByStatus(status!!, 0))
            userGamesFiltered.addAll(userGames.toList())

            runOnUiThread {

                getGamesOnBackground()

                listGameAdapter = ListGameAdapter(userGamesFiltered, R.layout.game_grid_item, launcher, true, ::notifyGameDetailCalled, ::checkLoadingFunction,)

                binding.rvAllGames.layoutManager = GridLayoutManager(applicationContext, 2)
                binding.rvAllGames.adapter = listGameAdapter
                updateGamesCount(sizeGames)

                filterBottomSheet = SearchGamesBottomSheet(this, userGamesFiltered, ::onFilterApplied)

                binding.pbLoadingMiddle.visibility = View.INVISIBLE

                binding.btnSearchFilter.setOnClickListener {
                    filterBottomSheet.show()
                }
            }
        }
    }

    private fun getGamesOnBackground() {

        if ((sizeGames - userGames.size) <= 0) {
            return
        }

        thread {
            val games = UserGameIntegration.getUserGameByStatus(status!!, userGames.size)

            userGamesFiltered.addAll(games.toList())

            runOnUiThread {

                binding.rvAllGames.adapter?.notifyItemRangeInserted(userGames.size, userGamesFiltered.size)

                userGames.addAll(games)

                binding.pbLoadingBottom.visibility = View.GONE

                loadedAll = userGames.size == sizeGames

                getGamesOnBackground()
            }
        }
    }

    private fun checkLoadingFunction() {
        if (!loadedAll) {
            binding.pbLoadingBottom.visibility = View.VISIBLE
        }
    }

    private fun notifyGameDetailCalled(position : Int, game : UserGame) {
        gameModified = Pair(position, game)
    }

    private fun updateNavigationBarHeight() {
        val statusBarSize = resources.getIdentifier("status_bar_height", "dimen", "android")

        val statusBarHeight = resources.getDimensionPixelSize(statusBarSize)

        if (statusBarHeight > 0) {
            binding.rootView.updatePadding(top = statusBarHeight)
        }
    }

    private fun onFilterApplied(platforms : List<String>, gameName : String?) {

        selectedPlatform = platforms

        val gameNameUpperCase = gameName?.uppercase()

        userGamesFiltered.clear()
        if (platforms.isEmpty() && (gameName == null || gameName == "")) {

            for (userGame in userGames) {
                userGamesFiltered.add(userGame)
            }
        }
        else {

            for (userGame in userGames) {

                if ((gameNameUpperCase != null && gameNameUpperCase != "")) {
                    if (userGame.game!!.name.uppercase().contains(gameNameUpperCase) && (platforms.isEmpty() || platforms.contains(userGame.platform))){
                        userGamesFiltered.add(userGame)
                    }
                }
                else if (platforms.isNotEmpty() && platforms.contains(userGame.platform)) {
                    userGamesFiltered.add(userGame)
                }
            }
        }

        listGameAdapter!!.notifyDataSetChanged()

        updateGamesCount()

        filterBottomSheet.dialog.dismiss()
    }

    private fun updateGamesCount() {
        binding.tvAllGamesCount.text = userGamesFiltered.size.toString()
    }

    private fun updateGamesCount(count : Int) {
        binding.tvAllGamesCount.text = count.toString()
    }
}