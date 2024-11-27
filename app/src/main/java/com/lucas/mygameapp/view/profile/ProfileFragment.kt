package com.lucas.mygameapp.view.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.lucas.mygameapp.R
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.databinding.FragmentLibraryBinding
import com.lucas.mygameapp.databinding.FragmentProfileBinding
import com.lucas.mygameapp.model.GameStatus
import com.lucas.mygameapp.model.User
import com.lucas.mygameapp.utils.BitmapUtil
import com.lucas.mygameapp.utils.MaskWatcher
import com.lucas.mygameapp.view.MainActivity
import com.lucas.mygameapp.view.gamedetail.GameDetailActivityContract
import java.io.File
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding

    private val launcher = registerForActivityResult(EditProfileActivityContract()) {
        if (it != null && it) {
            updateUserData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        updateUserData()
        updateGamesCount()

        return binding.root
    }

    private fun updateUserData() {

        val loggedUser = MainActivity.loggedUser
        binding.tvUserName.text = loggedUser.name
        binding.tvMemberSince.text = SimpleDateFormat("dd.MM.yyyy").format(loggedUser.createdDate!!)

        if (loggedUser.picturePath != null) {
            binding.imgUserPicture.setImageBitmap(BitmapUtil.generateBitmap(loggedUser.picturePath!!))
        }

        binding.imgUserPicture.setOnClickListener {
            launcher.launch(loggedUser.id)
        }
    }

    private fun updateGamesCount() {
        thread {
            val database = Database.getInstance(activity?.applicationContext!!)
            val userGames = database.userGameDao().getAll()

            val totalGames = userGames.size

            val playingGames = userGames.filter{it.status == GameStatus.PLAYING}.size

            val beatenGames = userGames.filter{it.status == GameStatus.FINISHED}.size

            requireActivity().runOnUiThread {
                binding.tvGamesCount.text = totalGames.toString()
                binding.tvGamesPlayingCount.text = playingGames.toString()
                binding.tvGamesBeatenCount.text = beatenGames.toString()
            }
        }
    }
}