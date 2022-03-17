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
    var totalNumPages: Int = 0

    init {
        val builder = OkHttpClient.Builder()

        // This will cause all network traffic to be logged to the console for easy debugging
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }


    //Load articles into top headlines page
    fun retrieveTopHeadlines(selectedCategory: String, apiKey: String, currPageNum: Int): List<Source>{
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?country=us&category=$selectedCategory&page=$currPageNum&apiKey=$apiKey")
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val headlinesList = mutableListOf<Source>()

            // Parse our way through the JSON hierarchy
            val json: JSONObject = JSONObject(responseBody)
            val articles: JSONArray = json.getJSONArray("articles")

            for (i in 0 until articles.length()) {
                val curr: JSONObject = articles.getJSONObject(i)
                val articleText: String = curr.getString("description")
                val articleSource: String = curr.getJSONObject("source").getString("name")
                val articleHeadline: String = curr.getString("title")
                val url: String = curr.getString("url")
                val thumbnailURL = curr.getString("urlToImage")
                val numArticles = json.getString("totalResults")

                //Display 20 per page
                totalNumPages = (numArticles.toInt() / 20) + 1 //rounds up from zero to 1
                val source = Source(
                    newsSource = articleSource,
                    sourceDescription = "",
                    newsContent = articleText,
                    newsHeadline = articleHeadline,
                    url = url,
                    term = "",
                    thumbnailURL = thumbnailURL,
                    sourceID = ""
                )

                headlinesList.add(source)
            }

            return headlinesList

        }

        return listOf()

    }


    //loads the different news sources based on category
    fun retrieveSources(selectedCategory: String, searchTerm: String, apiKey: String): List<Source> {
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/sources?country=us&category=$selectedCategory&apiKey=$apiKey")
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sources_list = mutableListOf<Source>()

            // Parse our way through the JSON hierarchy
            val json: JSONObject = JSONObject(responseBody)
            val sources: JSONArray = json.getJSONArray("sources")

            for (i in 0 until sources.length()) {
                val curr: JSONObject = sources.getJSONObject(i)
                val newsSource: String = curr.getString("name")
                val sourceDescription: String = curr.getString("description")
                val sourceID: String = curr.getString("id")

                val source = Source(
                    newsSource = newsSource,
                    sourceDescription = sourceDescription,
                    newsContent = "",
                    newsHeadline = "",
                    url = "",
                    term = searchTerm,
                    thumbnailURL ="",
                    sourceID = sourceID
                )

                sources_list.add(source)
            }

            return sources_list
        }

        return listOf()
    }

    //loads the articles from the selected source based on the search term
    fun retrieveSourcesArticles(sourceID: String, searchTerm: String, apiKey: String): List<Source> {
        //Don't include source in query if they chose to skip sources
        val request: Request = if(sourceID == ""){
            Request.Builder()
                .url("https://newsapi.org/v2/everything?language=en&q=$searchTerm&apiKey=$apiKey")
                .build()
        } else{
            Request.Builder()
                .url("https://newsapi.org/v2/everything?language=en&sources=$sourceID&q=$searchTerm&apiKey=$apiKey")
                .build()
        }

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val articles_list = mutableListOf<Source>()

            // Parse our way through the JSON hierarchy
            val json: JSONObject = JSONObject(responseBody)
            val articles: JSONArray = json.getJSONArray("articles")

            for (i in 0 until articles.length()) {
                val curr: JSONObject = articles.getJSONObject(i)
                val articleText: String = curr.getString("description")
                val articleSource: String = curr.getJSONObject("source").getString("name")
                val articleHeadline: String = curr.getString("title")
                val url: String = curr.getString("url")
                val thumbnailURL = curr.getString("urlToImage")
                val sourceID: String = curr.getJSONObject("source").getString("id")

                val source = Source(
                    newsSource = articleSource,
                    sourceDescription = "",
                    newsContent = articleText,
                    newsHeadline = articleHeadline,
                    url = url,
                    term = searchTerm,
                    thumbnailURL = thumbnailURL,
                    sourceID = sourceID
                )

                articles_list.add(source)
            }

            return articles_list

        }

        return listOf()
    }

    fun getMapSources(location: String, apiKey: String): List<Source> {
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/everything?qInTitle=$location&language=en&apiKey=$apiKey")
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sources_list = mutableListOf<Source>()

            // Parse our way through the JSON hierarchy
            val json: JSONObject = JSONObject(responseBody)
            val articles: JSONArray = json.getJSONArray("articles")

            for (i in 0 until articles.length()) {
                val curr: JSONObject = articles.getJSONObject(i)
                val articleText: String = curr.getString("description")
                val articleSource: String = curr.getJSONObject("source").getString("name")
                val articleHeadline: String = curr.getString("title")
                val url: String = curr.getString("url")
                val thumbnailURL = curr.getString("urlToImage")
                val sourceID: String = curr.getJSONObject("source").getString("id")

                val source = Source(
                    newsSource = articleSource,
                    sourceDescription = "",
                    newsContent = articleText,
                    newsHeadline = articleHeadline,
                    url = url,
                    term = "",
                    thumbnailURL = thumbnailURL,
                    sourceID = sourceID
                )
                sources_list.add(source)
            }
            return sources_list
        }
        return listOf()
    }
}