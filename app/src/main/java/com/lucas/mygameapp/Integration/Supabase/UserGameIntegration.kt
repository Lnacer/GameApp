package com.lucas.mygameapp.Integration.Supabase

import androidx.room.Query
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lucas.mygameapp.VO.UserGameAggregateCountVO
import com.lucas.mygameapp.VO.UserGameAggregateYearCountVO
import com.lucas.mygameapp.VO.UserGameSupabaseVO
import com.lucas.mygameapp.VO.UserGameVO
import com.lucas.mygameapp.model.GameStatus
import com.lucas.mygameapp.model.UserGame
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.ZoneId
import java.util.Date


class UserGameIntegration : SupabaseIntegration() {

    companion object {

        private val TABLE = "user_game"

        fun getByStatus(status : GameStatus, limit : Int) : List<UserGame> {

            val url = URL("$BASE_URL/$TABLE?select=id,game_id,status,platform,rating,game(id,name,cover_blob)&order=created_date.desc&status=eq.${status.printableName}&limit=$limit")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameSupabaseVO>::class.java)

                val userGames = mutableListOf<UserGame>()

                for (userGameVO in response) {
                    userGames.add(convertVoToEntity(userGameVO))
                }

                return userGames.toList()
            }

            throw Exception("Failed to get access to the server")
        }

        fun getUserGameById(id : Int) : UserGame {
            val url = URL("$BASE_URL/$TABLE?select=*,game(*)&id=eq.$id&limit=1")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameSupabaseVO>::class.java)

                if (response.isEmpty()) {
                    throw Exception("No User Game found with the id: ${id.toString()}")
                }

                return convertVoToEntity(response[0])
            }

            throw Exception("Failed to get access to the server")
        }

        fun getUserGameByGameId(gameId : Int) : UserGame? {
            val url = URL("$BASE_URL/$TABLE?select=*,game(*)&game_id=eq.${gameId}&limit=1")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameSupabaseVO>::class.java)

                if (response.isEmpty()) {
                    return null
                }

                return convertVoToEntity(response[0])
            }

            throw Exception("Failed to get access to the server")
        }

        fun getCounts(status : GameStatus? = null) : Map<String, Int> {

            var endpoint = "$BASE_URL/$TABLE?select=status,count()"

            if (status != null) {
                endpoint += "&status=eq.${status.printableName}"
            }

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameAggregateCountVO>::class.java)

                return response.associate { it.status to it.count }
            }

            throw Exception("Failed to get access to the server")
        }

        fun getAll() : List<UserGame> {
            val url = URL("$BASE_URL/$TABLE?select=id,game_id,status,platform,rating,game(name,cover_blob)&order=created_date.desc")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameSupabaseVO>::class.java)

                val userGames = mutableListOf<UserGame>()

                for (userGameVO in response) {
                    userGames.add(convertVoToEntity(userGameVO))
                }

                return userGames.toList()
            }

            throw Exception("Failed to get access to the server")
        }

        fun getUserGameByStatus(status: GameStatus, offset: Int, orderBy : String? = null) : List<UserGame> {

            var queryOrder = ""

            if (orderBy == null) {
                queryOrder = "created_date.desc"
            }
            else if (!queryOrder.contains("created_date")) {
                queryOrder = orderBy
                queryOrder += ",created_date.desc.nullslast"
            }

            val url = URL("$BASE_URL/$TABLE?status=eq.${status.printableName}&select=id,game_id,status,platform,rating,game(id,name,cover_blob)&order=${queryOrder}&limit=20&offset=$offset")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameSupabaseVO>::class.java)

                val userGames = mutableListOf<UserGame>()

                for (userGameVO in response) {
                    userGames.add(convertVoToEntity(userGameVO))
                }

                return userGames.toList()
            }

            throw Exception("Failed to get access to the server")
        }

        fun getTimelineGamesCount(status : GameStatus) : List<UserGameAggregateYearCountVO> {

            val yearField = if (status == GameStatus.FINISHED) "stop_playing_year" else "start_playing_year"

            val endpoint = "$BASE_URL/$TABLE?select=year:${yearField},count()&${yearField}=not.is.null&order=${yearField}.desc"

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameAggregateYearCountVO>::class.java)

                return response.toList()
            }

            throw Exception("Failed to get access to the server")
        }

        fun getTimelineGames(year : Int, orderByStartedDate : Boolean) : List<UserGame> {

            var orderBy = "start_playing.desc"
            var whereClause = "start_playing_year=eq.${year}"
            if (!orderByStartedDate) {
                orderBy = "stop_playing.desc"
                whereClause = "stop_playing_year=eq.${year}"
            }

            val endpoint = "$BASE_URL/$TABLE?select=start_playing,stop_playing,rating,platform,playing_time,game(id,name,cover_blob)&${whereClause}&order=${orderBy}"

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameSupabaseVO>::class.java)

                val userGames = mutableListOf<UserGame>()

                for (userGameVO in response) {
                    userGames.add(convertVoToEntity(userGameVO))
                }

                return userGames.toList()
            }

            throw Exception("Failed to get access to the server")
        }

        fun upsertUserGame(userGame : UserGame) {

            val userGameVO = convertUserGameToUserGameVo(userGame)

            var endpoint = "$BASE_URL/$TABLE?select=id"
            var method = "POST"

            var json = GsonBuilder().create().toJson(userGameVO)

            if (userGame.id != null) {
                method = "PATCH"
                endpoint += "&id=eq.${userGame.id}"
                json = GsonBuilder().serializeNulls().create().toJson(userGameVO)
            }

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = method
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)
            httpURLConnection.setRequestProperty("Content-Type", "application/json")
            httpURLConnection.setRequestProperty("Prefer", "return=representation")

            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(json)
            outputStreamWriter.flush()
            outputStreamWriter.close()

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserGameSupabaseVO>::class.java)

                if (response.isNotEmpty() && userGame.id == null) {
                    userGame.id = response[0].id
                }
            }
            else{
                throw Exception("Failed to create the user")
            }
        }

        fun deleteUserGame(gameId : Int) {
            val endpoint = "$BASE_URL/$TABLE?game_id=eq.$gameId"
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

        /*fun insertUserGames(userGames : List<UserGame>) {

            val userGamesVO = mutableListOf<UserGameVO>()

            for (userGame in userGames) {
                userGamesVO.add(convertUserGameToUserGameVo(userGame))
            }

            val endpoint = "$BASE_URL/$TABLE"
            val method = "POST"

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = method
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)
            httpURLConnection.setRequestProperty("Content-Type", "application/json")

            val json = GsonBuilder().serializeNulls().create().toJson(userGamesVO)

            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(json)
            outputStreamWriter.flush()
            outputStreamWriter.close()

            val responseCode = httpURLConnection.responseCode

            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                throw Exception("Failed to create the user")
            }
        }

        fun updatePlayingGames(userGames : List<UserGame>) {

            val userGamesVO = mutableListOf<UserGameVO>()

            for (userGame in userGames) {
                userGamesVO.add(convertUserGameToUserGameVo(userGame))
            }

            val endpoint = "$BASE_URL/$TABLE"
            val method = "PATCH"

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = method
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)
            httpURLConnection.setRequestProperty("Content-Type", "application/json")

            val json = GsonBuilder().serializeNulls().create().toJson(userGamesVO)

            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(json)
            outputStreamWriter.flush()
            outputStreamWriter.close()

            val responseCode = httpURLConnection.responseCode

            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                throw Exception("Failed to create the user")
            }
        }*/

        private fun convertVoToEntity(vo : UserGameSupabaseVO) : UserGame {
            val userGame = UserGame(null)

            if (vo.game != null) {
                userGame.game = GameIntegration.convertVoToEntity(vo.game!!)
            }

            userGame.createdDate = vo.created_date
            userGame.id = vo.id
            userGame.gameId = vo.game_id
            userGame.platform = vo.platform
            userGame.stopPlayingOn = vo.stop_playing
            userGame.playingTime = vo.playing_time
            userGame.progress = vo.progress
            userGame.rating = vo.rating
            userGame.startPlayingOn = vo.start_playing

            if (vo.status != null) {
                userGame.status = GameStatus.values().find { it.printableName == vo.status }
            }

            return userGame
        }

        private fun convertUserGameToUserGameVo(userGame : UserGame) : UserGameVO {

            val userGameVO = UserGameVO(userGame.gameId ?: userGame.game!!.id)
            userGameVO.id = userGame.id
            userGameVO.created_date = userGame.createdDate
            userGameVO.platform = userGame.platform
            userGameVO.rating = userGame.rating
            userGameVO.progress = userGame.progress
            userGameVO.playing_time = userGame.playingTime
            userGameVO.start_playing = userGame.startPlayingOn
            userGameVO.stop_playing = userGame.stopPlayingOn
            userGameVO.status = userGame.status?.printableName

            if (userGame.startPlayingOn != null) {
                val localDate = userGame.startPlayingOn!!.toInstant()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()
                userGameVO.start_playing_year = localDate.year
            }

            if (userGame.stopPlayingOn != null) {
                val localDate = userGame.stopPlayingOn!!.toInstant()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()
                userGameVO.stop_playing_year = localDate.year
            }

            return userGameVO
        }
    }
}