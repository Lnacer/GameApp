package com.lucas.mygameapp.Integration

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.lucas.mygameapp.VO.ScreenshotVO
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.User
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

private const val WHERE_SEARCH = "where game = {GAME_ID}"
private const val FIELDS = "fields url"
private const val SORT = "sort id asc"

class IgdbScreenshotIntegration : IgdbIntegration() {

    companion object {
        fun getGameScreenshots(gameId : Int, connectedUser : User) : MutableList<Bitmap> {
            val url = URL("$BASE_URL/screenshots")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true

            val body = "$FIELDS; $SORT; ${WHERE_SEARCH.replace("{GAME_ID}", gameId.toString())};"

            val postData: ByteArray = body.toByteArray(StandardCharsets.UTF_8)

            val inputStreamReader = getIgdbResponse(httpURLConnection, postData,connectedUser)

            val screenshotsVO = Gson().fromJson(inputStreamReader, Array<ScreenshotVO>::class.java)

            val images : MutableList<Bitmap> = mutableListOf()
            for (vo in screenshotsVO) {

                val bitmap = IgdbIntegration.getImageBitmap(
                    "https:${
                        vo.url.replace(
                            "t_thumb",
                            "t_screenshot_med"
                        )
                    }"
                )

                if (bitmap != null) {
                    images.add(bitmap)
                }
            }

            return images
        }
    }
}