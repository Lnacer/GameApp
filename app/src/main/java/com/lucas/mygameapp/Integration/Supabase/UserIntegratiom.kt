package com.lucas.mygameapp.Integration.Supabase

import com.google.gson.Gson
import com.lucas.mygameapp.VO.UserVO
import com.lucas.mygameapp.model.User
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

class UserIntegratiom : SupabaseIntegration() {
    companion object {

        private val TABLE = "user"

        fun getUser(email : String) : User {

            val url = URL("$BASE_URL/$TABLE?email=eq.$email")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {

                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                val response = Gson().fromJson(inputStreamReader, Array<UserVO>::class.java)

                if (response.isEmpty()) {
                    throw Exception("Email doesn't exist")
                }

                return convertUserVOtoUser(response.get(0))
            }

            throw Exception("Failed to get access to the server")
        }

        fun upsertUser(user : User) {
            val userVO = convertUsertoUserVO(user)

            val userId = userVO.id

            val isUpdate = userId != null

            var endpoint = "$BASE_URL/$TABLE"
            var method = "POST"

            if (isUpdate) {
                endpoint += "?id=eq.$userId"
                method = "PATCH"
            }

            val url = URL(endpoint)

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = method
            httpURLConnection.doOutput = true
            httpURLConnection.setRequestProperty("apikey", ANON_KEY)
            httpURLConnection.setRequestProperty("Content-Type", "application/json")

            val json = Gson().toJson(userVO)

            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(json)
            outputStreamWriter.flush()
            outputStreamWriter.close()

            val responseCode = httpURLConnection.responseCode

            if (responseCode != HttpURLConnection.HTTP_CREATED && responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                throw Exception("Failed to create the user")
            }
        }

        private fun convertUserVOtoUser(vo : UserVO) : User {
            val user = User(vo.name, vo.id!!)
            user.picturePath = vo.picture_path

            if (vo.picture != null) {
                user.picture = Base64.getDecoder().decode(vo.picture)
            }
            user.accessToken = vo.access_token
            user.email = vo.email
            user.createdDate = vo.created_date

            return user
        }

        private fun convertUsertoUserVO(user : User) : UserVO {
            val userVO = UserVO()
            userVO.name = user.name
            userVO.id = user.id
            userVO.picture_path = user.picturePath

            if (user.picture != null) {
                userVO.picture = Base64.getEncoder().encodeToString(user.picture)
            }

            userVO.access_token = user.accessToken
            userVO.email = user.email!!

            return userVO
        }
    }
}