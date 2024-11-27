package com.lucas.mygameapp.Integration.Supabase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lucas.mygameapp.VO.GameSupabaseVO
import com.lucas.mygameapp.VO.GameVO
import com.lucas.mygameapp.VO.PlatformVO
import com.lucas.mygameapp.model.Game
import com.lucas.mygameapp.model.Platform
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

class GameIntegration  : SupabaseIntegration() {

    companion object {

        private val TABLE = "game"

        fun insertGame(game : Game) {

            val gameVO = convertGameToGameVo(game)

            val endpoint = "$BASE_URL/$TABLE"
            val method = "POST"

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = method
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)
            httpURLConnection.setRequestProperty("Content-Type", "application/json")

            val json = GsonBuilder().serializeNulls().create().toJson(gameVO)

            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(json)
            outputStreamWriter.flush()
            outputStreamWriter.close()

            val responseCode = httpURLConnection.responseCode

            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                throw Exception("Failed to create the user")
            }
        }

        fun deleteGame(id : Int) {
            val endpoint = "$BASE_URL/$TABLE?id=eq.$id"
            val method = "DELETE"

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = method
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)
            httpURLConnection.setRequestProperty("Content-Type", "application/json")

            val responseCode = httpURLConnection.responseCode

            if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                throw Exception("Failed to create the user")
            }
        }

        fun updateGame(game : Game) {

            val gameVO = convertGameToGameVo(game)

            val gameId = game.id

            val endpoint = "$BASE_URL/$TABLE?id=eq.$gameId"
            val method = "PATCH"

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = method
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)
            httpURLConnection.setRequestProperty("Content-Type", "application/json")

            val json = GsonBuilder().serializeNulls().create().toJson(gameVO)

            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(json)
            outputStreamWriter.flush()
            outputStreamWriter.close()

            val responseCode = httpURLConnection.responseCode

            if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                throw Exception("Failed to create the user")
            }
        }

        internal fun convertVoToEntity(vo : GameSupabaseVO) : Game {
            val game = Game(vo.name!!, vo.id!!)

            game.platforms = vo.platforms
            game.genres = vo.genres
            game.summary = vo.summary
            game.developers = vo.involved_companies.filter { it.developer }.map { it.company.name!! }
            game.publishers = vo.involved_companies.filter { it.publisher }.map { it.company.name!! }
            game.coverUrl = vo.cover_url
            game.releaseDate = vo.first_release_date

            if (vo.cover_blob != null) {
                game.coverBlob = Base64.getDecoder().decode(vo.cover_blob)
            }

            return game
        }

        private fun convertGameToGameVo(game : Game) : GameSupabaseVO {
            val gameVO = GameSupabaseVO()
            gameVO.name = game.name
            gameVO.id = game.id
            gameVO.genres = game.genres
            gameVO.platforms = game.platforms
            gameVO.cover_url = game.coverUrl
            gameVO.first_release_date = game.releaseDate
            gameVO.summary = game.summary

            if (game.coverBlob != null) {
                gameVO.cover_blob = Base64.getEncoder().encodeToString(game.coverBlob)
            }

            return gameVO
        }
    }
}