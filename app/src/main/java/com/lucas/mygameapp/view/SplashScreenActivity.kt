package com.lucas.mygameapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lucas.mygameapp.Integration.IgdbIntegration
import com.lucas.mygameapp.Integration.IgdbPlatformIntegration
import com.lucas.mygameapp.Integration.Supabase.GameIntegration
import com.lucas.mygameapp.Integration.Supabase.PlatformIntegration
import com.lucas.mygameapp.Integration.Supabase.UserGameIntegration
import com.lucas.mygameapp.Integration.TwitchIntegration
import com.lucas.mygameapp.Integration.Supabase.UserIntegratiom
import com.lucas.mygameapp.database.Database
import com.lucas.mygameapp.databinding.ActivitySplashScreenBinding
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.User
import com.lucas.mygameapp.model.UserGame
import kotlin.concurrent.thread

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

        thread {

            val email = "lfabossa@hotmail.com"

            val createdUser = UserIntegratiom.getUser(email)

            runOnUiThread {
                MainActivity.loggedUser = createdUser
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun createPlatforms(connectedUser : User) {
        val platforms = IgdbPlatformIntegration.getPlatforms(connectedUser)

        PlatformIntegration.createPlatforms(platforms)
    }

    private fun createUser() {
        val accessVO = TwitchIntegration.getAccessToken()
        val user = User("default user", 1)
        user.email = "lfabossa@hotmail.com"
        user.accessToken = accessVO.access_token

        //delete the id
        UserIntegratiom.upsertUser(user)
    }
}