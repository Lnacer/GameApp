package com.lucas.mygameapp.Integration.Supabase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lucas.mygameapp.VO.PlatformVO
import com.lucas.mygameapp.VO.UserVO
import com.lucas.mygameapp.model.Platform
import com.lucas.mygameapp.model.User
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

class PlatformIntegration : SupabaseIntegration() {

    companion object {

        private val TABLE = "platform"

        fun getAllPlatforms() : List<Platform> {
            val url = URL("$BASE_URL/$TABLE?order=generation.desc.nullslast")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<PlatformVO>::class.java)

                val platforms = mutableListOf<Platform>()

                for (platformVO in response) {
                    platforms.add(convertPlatformVoToPlatform(platformVO))
                }

                return platforms.toList()
            }

            throw Exception("Failed to get access to the server")
        }

        fun createPlatforms(platforms : List<Platform>) {

            val platformsVO = mutableListOf<PlatformVO>()

            for (platform in platforms) {
                platformsVO.add(convertPlatformToPlatformVo(platform))
            }

            val endpoint = "$BASE_URL/$TABLE"
            val method = "POST"

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = method
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)
            httpURLConnection.setRequestProperty("Content-Type", "application/json")

            val json = GsonBuilder().serializeNulls().create().toJson(platformsVO)

            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(json)
            outputStreamWriter.flush()
            outputStreamWriter.close()

            val responseCode = httpURLConnection.responseCode

            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                throw Exception("Failed to create the user")
            }
        }

        private fun convertPlatformVoToPlatform(vo : PlatformVO) : Platform {
            val platform = Platform(vo.id!!)

            platform.name = vo.name
            platform.abbreviation = vo.abbreviation
            platform.generation = vo.generation
            platform.logoUrl = vo.platform_logo_url

            return platform
        }

        private fun convertPlatformToPlatformVo(platform : Platform) : PlatformVO {
            val platformVO = PlatformVO()
            platformVO.id = platform.id
            platformVO.name = platform.name
            platformVO.platform_logo_url = platform.logoUrl
            platformVO.abbreviation = platform.abbreviation
            platformVO.generation = platform.generation

            return platformVO
        }
    }
}