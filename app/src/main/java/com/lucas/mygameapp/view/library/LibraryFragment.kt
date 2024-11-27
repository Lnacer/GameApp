package com.lucas.mygameapp.view.library

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucas.mygameapp.Integration.Supabase.UserGameIntegration
import com.lucas.mygameapp.R
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.databinding.FragmentLibraryBinding
import com.lucas.mygameapp.model.GameStatus
import com.lucas.mygameapp.model.User
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.utils.BitmapUtil
import com.lucas.mygameapp.view.MainActivity
import com.lucas.mygameapp.view.adapter.ListGameAdapter
import com.lucas.mygameapp.view.allgames.AllGamesActivity
import com.lucas.mygameapp.view.gamedetail.GameDetailActivityContract
import com.lucas.mygameapp.view.profile.EditProfileActivityContract
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import kotlin.concurrent.thread


@Suppress("NAME_SHADOWING")
class LibraryFragment : Fragment() {

    private lateinit var binding : FragmentLibraryBinding
    private var gameModified : Pair<Int, UserGame>? = null
    private var wantGames : MutableList<UserGame> = mutableListOf()
    private var playingGames : MutableList<UserGame> = mutableListOf()
    private var beatenGames : MutableList<UserGame> = mutableListOf()
    private var pausedGames : MutableList<UserGame> = mutableListOf()
    private var stoppedGames : MutableList<UserGame> = mutableListOf()
    private var loggedUser : User? = null

    private val launcher = registerForActivityResult(GameDetailActivityContract()) {
        if (it != null) {
            if (it.status != gameModified?.second?.status) {
                when (gameModified?.second?.status) {
                    GameStatus.WANT -> {
                        wantGames.removeAt(gameModified?.first!!)
                        binding.rvWantGamesId.adapter?.notifyItemRemoved(gameModified?.first!!)
                    }
                    GameStatus.PLAYING -> {
                        playingGames.removeAt(gameModified?.first!!)
                        binding.rvPlayingGamesId.adapter?.notifyItemRemoved(gameModified?.first!!)
                    }
                    GameStatus.FINISHED -> {
                        beatenGames.removeAt(gameModified?.first!!)
                        binding.rvBeatenGamesId.adapter?.notifyItemRemoved(gameModified?.first!!)
                    }
                    GameStatus.PAUSED -> {
                        pausedGames.removeAt(gameModified?.first!!)
                        binding.rvPausedGames.adapter?.notifyItemRemoved(gameModified?.first!!)
                    }
                    GameStatus.STOPPED -> {
                        stoppedGames.removeAt(gameModified?.first!!)
                        binding.rvStoppedGames.adapter?.notifyItemRemoved(gameModified?.first!!)
                    }
                    else -> {}
                }

                updateGameChanged(it)
                //updateGamesCount()
            }
            else {
                when (gameModified?.second?.status) {
                    GameStatus.WANT -> {
                        wantGames[gameModified?.first!!] = it
                        binding.rvWantGamesId.adapter?.notifyItemChanged(gameModified?.first!!)
                    }
                    GameStatus.PLAYING -> {
                        playingGames[gameModified?.first!!] = it
                        binding.rvPlayingGamesId.adapter?.notifyItemChanged(gameModified?.first!!)
                    }
                    GameStatus.FINISHED -> {
                        beatenGames[gameModified?.first!!] = it
                        binding.rvBeatenGamesId.adapter?.notifyItemChanged(gameModified?.first!!)
                    }
                    GameStatus.PAUSED -> {
                        pausedGames[gameModified?.first!!] = it
                        binding.rvPausedGames.adapter?.notifyItemChanged(gameModified?.first!!)
                    }
                    GameStatus.STOPPED -> {
                        stoppedGames[gameModified?.first!!] = it
                        binding.rvStoppedGames.adapter?.notifyItemChanged(gameModified?.first!!)
                    }
                    else -> {}
                }
            }

            gameModified = null
        }
    }

    private val launcherEditProfile = registerForActivityResult(EditProfileActivityContract()) {
        if (it != null && it) {
            updateUserData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        updateWantGames()
        updatePlayingGames()
        updateBeatenGames()
        updatePausedGames()
        updateStoppedGames()
        updateGamesCount()

        updateUserData()

        binding.tvWantSeeAll.setOnClickListener {
            val intent = Intent(context, AllGamesActivity::class.java)
            intent.putExtra("status", GameStatus.WANT.printableName)
            startActivity(intent)
        }

        binding.tvPlayingSeeAll.setOnClickListener {
            val intent = Intent(context, AllGamesActivity::class.java)
            intent.putExtra("status", GameStatus.PLAYING.printableName)
            startActivity(intent)
        }

        binding.tvBeatenSeeAll.setOnClickListener {
            val intent = Intent(context, AllGamesActivity::class.java)
            intent.putExtra("status", GameStatus.FINISHED.printableName)
            startActivity(intent)
        }

        binding.tvPausedSeeAll.setOnClickListener {
            val intent = Intent(context, AllGamesActivity::class.java)
            intent.putExtra("status", GameStatus.PAUSED.printableName)
            startActivity(intent)
        }

        binding.tvStoppedSeeAll.setOnClickListener {
            val intent = Intent(context, AllGamesActivity::class.java)
            intent.putExtra("status", GameStatus.STOPPED.printableName)
            startActivity(intent)
        }

        return binding.root
    }

    private fun notifyGameDetailCalled(position : Int, game : UserGame) {
        gameModified = Pair(position, game)
    }

    private fun updateGamesCount() {

        thread {
            val countsMap = UserGameIntegration.getCounts()

            val wantCount = countsMap[GameStatus.WANT.printableName] ?: 0
            val playingCount = countsMap[GameStatus.PLAYING.printableName] ?: 0
            val beatenCount = countsMap[GameStatus.FINISHED.printableName] ?: 0
            val pausedCount = countsMap[GameStatus.PAUSED.printableName] ?: 0
            val stoppedCount = countsMap[GameStatus.STOPPED.printableName] ?: 0

            val gamesCount = wantCount + playingCount + beatenCount + pausedCount + stoppedCount

            activity?.runOnUiThread {
                binding.tvGamesCount.text = gamesCount.toString()
                binding.tvGamesPlayingCount.text = playingCount.toString()
                binding.tvGamesBeatenCount.text = beatenCount.toString()

                binding.tvWantGamesCount.text = "(${wantCount.toString()})"
                binding.tvPlayingGamesCount.text = "(${playingCount.toString()})"
                binding.tvBeatenGamesCount.text = "(${beatenCount.toString()})"
                binding.tvPausedGamesCount.text = "(${pausedCount.toString()})"
                binding.tvStopedGamesCount.text = "(${stoppedCount.toString()})"
            }
        }
    }

    private fun updateGameChanged(userGame: UserGame) {
        when (userGame.status) {
            GameStatus.WANT -> {
                wantGames.add(0, userGame)
                binding.rvWantGamesId.adapter?.notifyItemInserted(0)
            }
            GameStatus.PLAYING -> {
                playingGames.add(0, userGame)
                binding.rvPlayingGamesId.adapter?.notifyItemInserted(0)
            }
            GameStatus.FINISHED -> {
                beatenGames.add(0, userGame)
                binding.rvBeatenGamesId.adapter?.notifyItemInserted(0)
            }
            GameStatus.PAUSED -> {
                pausedGames.add(0, userGame)
                binding.rvPausedGames.adapter?.notifyItemInserted(0)
            }
            GameStatus.STOPPED -> {
                stoppedGames.add(0, userGame)
                binding.rvStoppedGames.adapter?.notifyItemInserted(0)
            }
            else -> {}
        }
    }

    private fun updateUserData() {
        loggedUser = MainActivity.loggedUser

        binding.tvUserName.text = loggedUser!!.name
        binding.tvMemberSince.text = SimpleDateFormat("dd.MM.yyyy").format(loggedUser!!.createdDate!!)

        if (loggedUser!!.picture != null) {
            val bitmap = BitmapFactory.decodeByteArray(loggedUser!!.picture, 0, loggedUser!!.picture!!.size)

            if (bitmap != null) {
                binding.imgUserPicture.setImageBitmap(bitmap)
            }
        }

        binding.imgUserPicture.setOnClickListener {
            launcherEditProfile.launch(loggedUser!!.id)
        }
    }

    private fun updateWantGames() {
        thread {
            wantGames = UserGameIntegration.getByStatus(GameStatus.WANT, 20).toMutableList()

            activity?.runOnUiThread {
                binding.rvWantGamesId.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvWantGamesId.adapter = ListGameAdapter(wantGames, R.layout.game_row_item, launcher, false, ::notifyGameDetailCalled)

                binding.llNoWantGames.llNoGames.visibility = if (wantGames.size > 0) View.GONE else View.VISIBLE
                binding.pbWantGames.visibility = View.GONE
            }
        }
    }

    private fun updatePlayingGames() {
        thread {
            playingGames = UserGameIntegration.getByStatus(GameStatus.PLAYING, 20).toMutableList()

            activity?.runOnUiThread {
                binding.rvPlayingGamesId.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvPlayingGamesId.adapter = ListGameAdapter(playingGames, R.layout.game_row_item, launcher, false, ::notifyGameDetailCalled)

                binding.llNoPlayingGames.llNoGames.visibility = if (playingGames.size > 0) View.GONE else View.VISIBLE
                binding.pbPlayingGames.visibility = View.GONE
            }
        }
    }

    private fun updateBeatenGames() {
        thread {
            beatenGames = UserGameIntegration.getByStatus(GameStatus.FINISHED, 20).toMutableList()

            activity?.runOnUiThread {
                binding.rvBeatenGamesId.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvBeatenGamesId.adapter = ListGameAdapter(beatenGames, R.layout.game_row_item, launcher, false, ::notifyGameDetailCalled)

                binding.llNoBeatenGames.llNoGames.visibility = if (beatenGames.size > 0) View.GONE else View.VISIBLE
                binding.pbBeatenGames.visibility = View.GONE
            }
        }
    }

    private fun updatePausedGames() {
        thread {
            pausedGames = UserGameIntegration.getByStatus(GameStatus.PAUSED, 20).toMutableList()

            activity?.runOnUiThread {
                binding.rvPausedGames.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvPausedGames.adapter = ListGameAdapter(pausedGames, R.layout.game_row_item, launcher, false, ::notifyGameDetailCalled)

                binding.llPauseddLayout.visibility = if (pausedGames.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun updateStoppedGames() {
        thread {
            stoppedGames = UserGameIntegration.getByStatus(GameStatus.STOPPED, 20).toMutableList()

            activity?.runOnUiThread {
                binding.rvStoppedGames.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.rvStoppedGames.adapter = ListGameAdapter(stoppedGames, R.layout.game_row_item, launcher, false, ::notifyGameDetailCalled)

                binding.llStoppedLayout.visibility = if (stoppedGames.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }
}