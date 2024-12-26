package com.lucas.mygameapp.Integration

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.lucas.mygameapp.Integration.Supabase.UserIntegratiom
import com.lucas.mygameapp.VO.GameVO
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.Platform
import com.lucas.mygameapp.model.User
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Date

private const val CLIENT_ID = "5uqgz9o8v8ccekz3z4w46s2jw23th8"
private const val GAMES_WHERE_SEARCH = "where (name ~ *\"{SEARCH_STRING}\"* | alternative_names.name ~ *\"{SEARCH_STRING}\"*) & platforms.abbreviation != null"
private const val LIMIT = "limit 10"
private const val OFFSET = "offset {OFFSET_SIZE}"
private val FIELDS = listOf("name", "first_release_date", "genres.name", "platforms.abbreviation", "cover.url", "summary", "involved_companies.publisher", "involved_companies.developer", "involved_companies.company.name")

open class IgdbIntegration {

    companion object {

        @JvmStatic
        protected val BASE_URL = "https://api.igdb.com/v4"

        private fun getRefreshedToken(connectedUser: User) : String {
            val accessVO = TwitchIntegration.getAccessToken()

            connectedUser.accessToken = accessVO.access_token

            UserIntegratiom.upsertUser(connectedUser)

            return accessVO.access_token
        }

        private fun convertGameVOtoGame(gameVO : GameVO) : Game {
            val game = Game(gameVO.name!!, gameVO.id!!)
            game.coverUrl = gameVO.cover?.url

            if (gameVO.first_release_date != null) {
                game.releaseDate = Date(gameVO.first_release_date?.toLong()!! * 1000)
            }

            game.summary = gameVO.summary

            val platforms = mutableListOf<String>()
            for (platform in gameVO.platforms) {
                if (platform.abbreviation != null && platform.abbreviation != "") {
                    platforms.add(platform.abbreviation!!)
                }
            }
            game.platforms = platforms

            val genres = mutableListOf<String>()
            for (genre in gameVO.genres) {
                if (genre.name != null && genre.name != "") {
                    genres.add(genre.name!!)
                }
            }
            game.genres = genres

            val developers = mutableListOf<String>()
            val publishers = mutableListOf<String>()
            for (company in gameVO.involved_companies.sortedBy { it.company.id }) {
                if (company.company.name == null)
                    continue

                if (company.company.name != null && company.developer ) {
                    developers.add(company.company.name!!)
                }

                if (company.company.name != null && company.publisher) {
                    publishers.add(company.company.name!!)
                }
            }
            game.developers = developers
            game.publishers = publishers

            if (game.coverUrl != null) {
                game.coverBitmap = getImageBitmap("https:${game.coverUrl?.replace("t_thumb","t_cover_big")}")

                if (game.coverBitmap != null) {
                    val outputStream = ByteArrayOutputStream()
                    game.coverBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    game.coverBlob = outputStream.toByteArray()
                }
            }

            return game
        }

        @JvmStatic
        protected fun getIgdbResponse(httpURLConnection : HttpURLConnection, postData: ByteArray, connectedUser: User) : InputStreamReader {

            connectedUser.accessToken = connectedUser.accessToken ?: getRefreshedToken(connectedUser)

            try {
                return getIgdbResponse(httpURLConnection, postData, connectedUser.accessToken!!)
            }
            catch (ex : Exception) {
                if (ex.message == "Invalid Token") {
                    connectedUser.accessToken = getRefreshedToken(connectedUser)

                    val newHttpURLConnection = (httpURLConnection.url.openConnection() as HttpURLConnection)
                    newHttpURLConnection.requestMethod = httpURLConnection.requestMethod
                    newHttpURLConnection.doOutput = httpURLConnection.doOutput

                    return getIgdbResponse(newHttpURLConnection, postData, connectedUser.accessToken!!)
                }

                throw ex
            }
        }

        private fun getIgdbResponse(httpURLConnection : HttpURLConnection, postData: ByteArray, accessToken : String) : InputStreamReader {

            httpURLConnection.setRequestProperty("Client-ID", CLIENT_ID)
            httpURLConnection.setRequestProperty("Authorization", "Bearer $accessToken")

            val outputStream = DataOutputStream(httpURLConnection.outputStream)
            outputStream.write(postData)
            outputStream.flush()

            val responseCode = httpURLConnection.responseCode

            val message = httpURLConnection.responseMessage

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return InputStreamReader(httpURLConnection.inputStream, "UTF-8")
            }
            else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw Exception("Invalid Token")
            }

            throw Exception("Unknown error");
        }

        fun getListOfGames(search : String, platforms : List<Platform>, listSize : Int, connectedUser: User) : MutableList<Game> {

            val userToken = connectedUser.accessToken ?: getRefreshedToken(connectedUser)

            val url = URL("$BASE_URL/games")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true

            var gamesWhereSearch = GAMES_WHERE_SEARCH
            if (platforms.isNotEmpty()) {
                gamesWhereSearch += " & platforms = (${platforms.map { it.id }.joinToString(",")})"
            }

            val updatedSearch = search.trim().replace(' ', '%')

            val body = "fields ${FIELDS.joinToString(",")}; ${gamesWhereSearch.replace("{SEARCH_STRING}", updatedSearch.trim())}; $LIMIT; ${OFFSET.replace("{OFFSET_SIZE}", listSize.toString())};"

            val postData: ByteArray = body.toByteArray(StandardCharsets.UTF_8)

            val inputStreamReader = getIgdbResponse(httpURLConnection, postData, userToken)
            val gamesVO = Gson().fromJson(inputStreamReader, Array<GameVO>::class.java)

            val responseGames : MutableList<Game> = mutableListOf()

            for (gameVO in gamesVO) {

                if (gameVO.cover == null) {
                    continue
                }

                responseGames.add(convertGameVOtoGame(gameVO))
            }

            return responseGames
        }

        fun getGameById(id : Int, accessToken: String) : Game {

            val url = URL("$BASE_URL/games")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true

            val body = "fields ${FIELDS.joinToString(",")}; where id = $id; $LIMIT; limit 1;"

            val postData: ByteArray = body.toByteArray(StandardCharsets.UTF_8)

            val inputStreamReader = getIgdbResponse(httpURLConnection, postData, accessToken)
            val gamesVO = Gson().fromJson(inputStreamReader, Array<GameVO>::class.java)

            if (gamesVO.isNotEmpty()) {
                return convertGameVOtoGame(gamesVO[0])
            }

            throw Exception("No game found with the id: $id")
        }

        fun getImageBitmap(coverUrl : String) : Bitmap? {
            try {
                val url = URL(coverUrl)

                return BitmapFactory.decodeStream(url.openConnection().getInputStream())
            }
            catch (ex : Exception) {
                return null
            }
        }
    }
}