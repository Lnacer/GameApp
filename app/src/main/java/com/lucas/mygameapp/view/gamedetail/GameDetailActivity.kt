package com.lucas.mygameapp.view.gamedetail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucas.mygameapp.Integration.IgdbIntegration
import com.lucas.mygameapp.Integration.IgdbScreenshotIntegration
import com.lucas.mygameapp.Integration.Supabase.GameIntegration
import com.lucas.mygameapp.Integration.Supabase.UserGameIntegration
import com.lucas.mygameapp.R
import com.lucas.mygameapp.databinding.ActivityGameDetailBinding
import com.lucas.mygameapp.databinding.PlatformTagBinding
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.GameStatus
import com.lucas.mygameapp.model.UserGame
import com.lucas.mygameapp.utils.DateUtil
import com.lucas.mygameapp.utils.DateUtil.Companion.toDate
import com.lucas.mygameapp.view.MainActivity
import com.lucas.mygameapp.view.adapter.ScreenshotSliderAdapter
import com.lucas.mygameapp.view.adapter.ScreenshotsAdapter
import com.lucas.mygameapp.view.bottomsheet.BottomSheet
import com.lucas.mygameapp.view.gamedetail.bottomsheet.GameDetailOptionsBottomSheet
import com.lucas.mygameapp.view.gamedetail.bottomsheet.GameRatingBottomSheet
import com.lucas.mygameapp.view.gamedetail.bottomsheet.GameStatusBottomSheet
import com.lucas.mygameapp.view.gamedetail.bottomsheet.PlatformsBottomSheet
import com.lucas.mygameapp.view.gamedetail.bottomsheet.PlayerDataBottomSheet
import jp.wasabeef.blurry.Blurry
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import kotlin.concurrent.thread

class GameDetailActivity : AppCompatActivity() {

    private val DEFAULT_UPDATE_STATUS_BUTTON_TEXT = "Add to"
    private val DEFAULT_UPDATE_STATUS_BUTTON_ICON = R.drawable.add_24px
    private val DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy")

    private lateinit var binding : ActivityGameDetailBinding
    private var gameId : Int = 0
    private var userGameId : Int = 0

    private var userGame : UserGame? = null
    private var bottomWindowSize = 0
    private var isKeyboardOpen = false
    private var bottomSheet : BottomSheet? = null
    private var keyboardSize = 0
    private var screenshotFullscreen = false
    private var priviousStatus : GameStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        binding.svGameDetail.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY >= binding.ivBannerImage.bottom - binding.llDetailPageBackButton.top) {
                binding.cvDetailPageFixed.visibility = View.VISIBLE

                window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
            else {
                binding.cvDetailPageFixed.visibility = View.INVISIBLE
                window.insetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
        }

        binding.rlBackground.setOnClickListener {
            closeScreenshot()
        }

        binding.vpScreenshot.setOnClickListener {
            closeScreenshot()
        }

        setKeyboardViewChangedListener()

        userGameId = intent.getIntExtra("userGameId", 0)
        gameId = intent.getIntExtra("gameId", 0)

        getGameScreenshots()

        getUserGameDetail()

        binding.llDetailPageBackButton.setOnClickListener {
            backPressed()
        }

        binding.btnHeaderBack.setOnClickListener {
            backPressed()
        }

        binding.btnAddGameTo.setOnClickListener {
            openStatusBottomSheet()
        }

        binding.btnOptions.setOnClickListener {
            bottomSheet = GameDetailOptionsBottomSheet(this)
            (bottomSheet as GameDetailOptionsBottomSheet).show()
        }
    }

    override fun onBackPressed() {

        if (!screenshotFullscreen) {
            val resultIntent = Intent()
            resultIntent.putExtra("userGame", userGame)
            setResult(RESULT_OK, resultIntent)

            super.onBackPressed()
        }
        else {
            closeScreenshot()
        }
    }

    private fun setBaseGameDetail() {
        binding.tvGameName.text = userGame?.game?.name
        binding.tvHeaderGameName.text = userGame?.game?.name

        if (userGame?.game?.publishers != null && userGame?.game?.publishers!!.isNotEmpty()) {
            binding.tvFirstPublisher.text = userGame?.game?.publishers!![0]
        }
        else {
            binding.tvFirstPublisher.text = ""
        }

        if (userGame?.game?.platforms != null) {
            for (platform in userGame?.game?.platforms!!) {
                val tagBinding = PlatformTagBinding.inflate(layoutInflater)
                tagBinding.tvPlatformName.text = platform

                binding.fbPlataforms.addView(tagBinding.root)
            }
        }

        if (userGame?.game?.releaseDate != null) {
            binding.tvFirstReleaseDate.text = DATE_FORMAT.format(userGame?.game?.releaseDate!!)
        }

        if (userGame?.game?.coverBitmap != null) {
            binding.ivBannerImage.setImageBitmap(userGame?.game?.coverBitmap)
            binding.ivCoverImage.setImageBitmap(userGame?.game?.coverBitmap)

            val a = userGame?.game?.coverBlob

            Blurry.with(this)
            .sampling(8)
            .from(userGame?.game?.coverBitmap)
            .into(binding.ivBannerImage)
        }
    }

    private fun updateGameDetailViews() {
        binding.cvGameInfo.visibility = View.VISIBLE
        binding.tvGameSummary.text = userGame?.game?.summary
        if (userGame?.game?.releaseDate != null) {
            binding.tvGameReleaseDate.text = DATE_FORMAT.format(userGame?.game?.releaseDate!!)
        }
        binding.tvGameDevelopers.text = userGame?.game?.developers?.joinToString("\r\n")
        binding.tvGamePublishers.text = userGame?.game?.publishers?.joinToString("\r\n")
        binding.tvGameGenres.text = userGame?.game?.genres?.joinToString("\r\n")
        binding.tvGamePlatforms.text = userGame?.game?.platforms?.joinToString("\r\n")
    }

    private fun getGameScreenshots() {
        thread {
            val screenshots = IgdbScreenshotIntegration.getGameScreenshots(gameId, MainActivity.loggedUser)

            runOnUiThread {
                val viewPagerAdapter = ScreenshotSliderAdapter(this, screenshots, ::closeScreenshot)
                binding.vpScreenshot.adapter = viewPagerAdapter

                binding.rvGameScreenshots.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
                binding.rvGameScreenshots.adapter = ScreenshotsAdapter(screenshots, ::onScreenshotClicked)
                binding.pbScreenshotsLoading.visibility = View.GONE
            }
        }
    }

    private fun getUserGameDetail() {
        thread {
            if (userGameId != 0) {
                userGame = UserGameIntegration.getUserGameById(userGameId)
            }
            else {
                userGame = UserGameIntegration.getUserGameByGameId(gameId)
            }

            if (userGame == null) {
                val game = IgdbIntegration.getGameById(gameId, MainActivity.loggedUser.accessToken!!)
                userGame = UserGame(game)
            }

            runOnUiThread {
                setBaseGameDetail()
                updateUserDetailViews()
                updateGameDetailViews()

                binding.pbGameDetail.visibility = View.GONE
                binding.flGameDetailFull.visibility = View.VISIBLE
            }
        }
    }

    private fun setKeyboardViewChangedListener() {
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            window.decorView.getWindowVisibleDisplayFrame(rect)

            if (rect.bottom >= bottomWindowSize) {
                bottomWindowSize = rect.bottom

                if (isKeyboardOpen) {
                    bottomSheet?.keyboardVisibilityChange(false, 0)
                }

                isKeyboardOpen = false
            }
            else {
                if (!isKeyboardOpen) {
                    keyboardSize = bottomWindowSize - rect.bottom

                    bottomSheet?.keyboardVisibilityChange(true, keyboardSize)
                }

                isKeyboardOpen = true
            }
        }
    }

    private fun backPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("userGame", userGame)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun onScreenshotClicked(index: Int) {
        binding.vpScreenshot.currentItem = index

        binding.rlBackground.visibility = View.VISIBLE
        binding.svGameDetail.scrollable = false
        binding.cvDetailPageFixed.elevation = 0f
        screenshotFullscreen = true
    }

    private fun closeScreenshot() {
        binding.rlBackground.visibility = View.INVISIBLE
        binding.svGameDetail.scrollable = true
        binding.cvDetailPageFixed.elevation = 5f
        screenshotFullscreen = false
    }

    fun updateUserDetailViews() {

        if (userGame?.status == null) {
            binding.btnAddGameTo.text = DEFAULT_UPDATE_STATUS_BUTTON_TEXT
            binding.btnAddGameTo.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, DEFAULT_UPDATE_STATUS_BUTTON_ICON), null, null, null)
        }
        else {
            binding.btnAddGameTo.text = userGame?.status?.printableName
            binding.btnAddGameTo.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, userGame?.status?.icon!!), null, null, null)
        }

        if (userGame?.status == null || userGame?.status == GameStatus.WANT) {
            binding.btnOptions.visibility = View.GONE
            binding.cvGameDetails.visibility = View.GONE
        }
        else {
            binding.btnOptions.visibility = View.VISIBLE
            binding.cvGameDetails.visibility = View.VISIBLE

            binding.tvStartOn.text = if(userGame?.startPlayingOn != null) DATE_FORMAT.format(userGame?.startPlayingOn!!) else "-"
            binding.tvFinishedOn.text = if(userGame?.stopPlayingOn != null) DATE_FORMAT.format(userGame?.stopPlayingOn!!) else "-"
            binding.tvPlayTime.text = if(userGame?.playingTime != null) userGame?.playingTime?.toString() + " hs" else "-"
            binding.tvProgress.text = if(userGame?.progress != null) userGame?.progress.toString() + "%" else "-"
        }

        binding.tvPlatformName.text = userGame?.platform
        binding.tvPlatformName.visibility = if (userGame?.platform != null) View.VISIBLE else View.GONE

        if (userGame?.rating != null) {
            val intRating = userGame?.rating!!.toInt()

            binding.tvRating.text = if (userGame?.rating!! - intRating == 0f) intRating.toString() else userGame?.rating?.toString()
            binding.tvRating.visibility = View.VISIBLE
        }
        else {
            binding.tvRating.visibility = View.GONE
        }
    }

    fun updateGameStatus(statusUpdated : GameStatus?) {

        if (userGame?.status == GameStatus.WANT && statusUpdated == GameStatus.PLAYING) {
            userGame!!.startPlayingOn = LocalDate.now().toDate()
        }
        else if (userGame?.status == GameStatus.PLAYING && statusUpdated == GameStatus.FINISHED) {
            userGame!!.stopPlayingOn = LocalDate.now().toDate()
        }

        if (userGame?.status != statusUpdated) {
            userGame?.createdDate = DateUtil.LocalDateToDate(LocalDate.now())
        }

        userGame?.status = statusUpdated

        thread {
            if (userGame?.status != null) {

                if (userGame?.id == null) {
                    userGame?.createdDate = DateUtil.LocalDateToDate(LocalDate.now())
                    userGame?.gameId = userGame?.game?.id
                    GameIntegration.insertGame(userGame?.game!!)
                }

                UserGameIntegration.upsertUserGame(userGame!!)
            } else {
                val gameId = userGame?.game?.id!!
                UserGameIntegration.deleteUserGame(gameId)
                GameIntegration.deleteGame(gameId)
            }
        }

        updateUserDetailViews()
    }

    fun updateGamePlatform(platform : String?) {
        userGame?.platform = platform

        thread {
            UserGameIntegration.upsertUserGame(userGame!!)
        }

        bottomSheet?.dialog?.dismiss()
        bottomSheet = null

        updateUserDetailViews()
    }

    fun updateGameRating(rating : Float?) {
        userGame?.rating = rating

        thread {
            UserGameIntegration.upsertUserGame(userGame!!)
        }

        bottomSheet?.dialog?.dismiss()
        bottomSheet = null

        updateUserDetailViews()
    }

    fun openStatusBottomSheet() {
        bottomSheet?.dialog?.dismiss()

        bottomSheet = GameStatusBottomSheet(userGame?.status, this)
        (bottomSheet as GameStatusBottomSheet).show()
    }

    fun openPlayerDataBottomSheet() {
        bottomSheet?.dialog?.dismiss()

        bottomSheet = PlayerDataBottomSheet(userGame!!, this)
        (bottomSheet as PlayerDataBottomSheet).show()
    }

    fun openPlatformsBottomSheet() {
        bottomSheet?.dialog?.dismiss()

        bottomSheet = PlatformsBottomSheet(userGame!!,this)
        (bottomSheet as PlatformsBottomSheet).show()
    }

    fun openRatingBottomSheet() {
        bottomSheet?.dialog?.dismiss()

        bottomSheet = GameRatingBottomSheet(userGame?.rating, this)
        (bottomSheet as GameRatingBottomSheet).show()
    }
}