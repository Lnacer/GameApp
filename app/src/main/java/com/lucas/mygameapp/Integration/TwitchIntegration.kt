package com.lucas.mygameapp.Integration

import com.google.gson.Gson
import com.lucas.mygameapp.VO.AccessVO
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private val BASE_URL = "https://id.twitch.tv"
private val CLIENT_ID = "5uqgz9o8v8ccekz3z4w46s2jw23th8"
private val CLIENT_SECRET = "iehl7asex764atchohwuo7v2ftrlze"
private val GRANT_TYPE = "client_credentials"

class TwitchIntegration {
    companion object {

        fun getAccessToken() : AccessVO {
            val url = URL("$BASE_URL/oauth2/token?client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&grant_type=$GRANT_TYPE")

            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStreamReader = InputStreamReader(httpURLConnection.inputStream, "UTF-8")

                return Gson().fromJson(inputStreamReader, AccessVO::class.java)
            }

            throw Exception("Failed to get access to the server")
        }
    }
}