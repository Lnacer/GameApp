package com.lucas.mygameapp.Integration

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.lucas.mygameapp.VO.PlatformVO
import com.lucas.mygameapp.VO.ScreenshotVO
import com.lucas.mygameapp.model.Platform
import com.lucas.mygameapp.model.User
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

private const val FIELDS = "fields id, abbreviation, name, generation, platform_logo.url"
private const val SORT = "sort generation desc"
private const val LIMIT = "limit 500"

class IgdbPlatformIntegration : IgdbIntegration() {

    companion object {
        fun getPlatforms(connectedUser : User) : MutableList<Platform> {
            val url = URL("$BASE_URL/platforms")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true

            val body = "$FIELDS; $SORT; $LIMIT;"

            val postData: ByteArray = body.toByteArray(StandardCharsets.UTF_8)

            val inputStreamReader = getIgdbResponse(httpURLConnection, postData, connectedUser)

            val platformVO = Gson().fromJson(inputStreamReader, Array<PlatformVO>::class.java)

            val platforms : MutableList<Platform> = mutableListOf()
            for (vo in platformVO) {
                platforms.add(convertPlatformVOtoPlatform(vo))
            }

            return platforms
        }

        private fun convertPlatformVOtoPlatform(vo : PlatformVO) : Platform {
            val platform = Platform(vo.id!!)
            platform.name = vo.name
            platform.abbreviation = vo.abbreviation
            platform.generation = vo.generation
            platform.logoUrl = vo.platform_logo_url

            return platform
        }
    }
}