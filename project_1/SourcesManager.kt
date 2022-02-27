package com.example.project_1

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log

class SourcesManager {

    private val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()

        // This will cause all network traffic to be logged to the console for easy debugging
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    fun retrieveSources(selectedCategory: String, apiKey: String): List<Source> {
        //val radius = "30mi"
        //val searchQuery = "Android"
        Log.d("ChosenCategory", selectedCategory)
        // Form the Search Tweets request per the docs at: https://developer.twitter.com/en/docs/tweets/search/api-reference/get-search-tweets.html
        // The "Authorization" header here is similar to an API Key... we'll see with Lecture 7.
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/sources?country=us&category=$selectedCategory&apiKey=$apiKey")
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sources_list = mutableListOf<Source>()

            // Parse our way through the JSON hierarchy, picking out what we need from each Tweet
            val json: JSONObject = JSONObject(responseBody)
            val sources: JSONArray = json.getJSONArray("sources")

            for (i in 0 until sources.length()) {
                val curr: JSONObject = sources.getJSONObject(i)
                val newsSource: String = curr.getString("name")
                val sourceDescription: String = curr.getString("description")


                val source = Source(
                    newsSource = newsSource,
                    sourceDescription = sourceDescription,
                    newsContent = "",
                    newsHeadline = "",
                    url = ""
                )

                sources_list.add(source)
            }

            return sources_list
        }

        return listOf()
    }

    fun getMapSources(location: String, apiKey: String): List<Source> {
        //val radius = "30mi"
        //val searchQuery = "Android"
        Log.d("Location", location)
        // Form the Search Tweets request per the docs at: https://developer.twitter.com/en/docs/tweets/search/api-reference/get-search-tweets.html
        // The "Authorization" header here is similar to an API Key... we'll see with Lecture 7.
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/everything?qInTitle=$location&language=en&apiKey=$apiKey")
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sources_list = mutableListOf<Source>()

            // Parse our way through the JSON hierarchy, picking out what we need from each Tweet
            val json: JSONObject = JSONObject(responseBody)
            val articles: JSONArray = json.getJSONArray("articles")

            for (i in 0 until articles.length()) {
                val curr: JSONObject = articles.getJSONObject(i)
                val articleText: String = curr.getString("description")
                val articleSource: String = curr.getJSONObject("source").getString("name")
                val articleHeadline: String = curr.getString("title")
                val url: String = curr.getString("url")


                val source = Source(
                    newsSource = articleSource,
                    sourceDescription = "",
                    newsContent = articleText,
                    newsHeadline = articleHeadline,
                    url = url
                )

                sources_list.add(source)
            }

            return sources_list
        }

        return listOf()
    }
}