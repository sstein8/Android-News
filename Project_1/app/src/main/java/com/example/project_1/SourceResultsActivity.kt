package com.example.project_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync

class SourceResultsActivity : AppCompatActivity() {
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var progressBarArticles: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source_results)

        articlesRecyclerView  = findViewById(R.id.articlesRecyclerView)

        //Get the source name and the search term
        val searchTerm: String = intent.getStringExtra("SearchInput")!!
        val sourceName: String = intent.getStringExtra("SourceName")!!
        val sourceID: String = intent.getStringExtra("SourceID")!!
        val sourcesManager = SourcesManager()
        val apiKey = getString(R.string.NEWS_API_KEY)
        progressBarArticles = findViewById(R.id.progressBarArticles)



        //Set title of the page
        if(sourceName == ""){ //if the user clicked skip source
            supportActionBar?.title = "Search results for $searchTerm"
            //display the recycler view that fetches articles with no particular source
        }
        else{
            supportActionBar?.title = "$sourceName results for $searchTerm"
        }
        progressBarArticles.visibility = View.VISIBLE
        doAsync {
            // Use source manager to get articles from the api.
            // If there is network connection issues, the catch-block will fire and we'll show the user an error message.

            val articlesList: List<Source> = try {
                sourcesManager.retrieveSourcesArticles(sourceID, searchTerm, apiKey)
            } catch(exception: Exception) {
                Log.e("SourcesActivity", "Retrieving Sources failed", exception)
                listOf<Source>()
            }
            progressBarArticles.visibility = View.INVISIBLE
            runOnUiThread {
                if (articlesList.isNotEmpty()) {
                    val source_adapter = MapSourceAdapter(articlesList)
                    articlesRecyclerView.adapter = source_adapter
                    //The LinearLayoutManager is used to set scroll direction (default is horizontal)
                    articlesRecyclerView.layoutManager = LinearLayoutManager(this@SourceResultsActivity)
                } else {
                    Toast.makeText(
                        this@SourceResultsActivity,
                        "Failed to retrieve articles!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }
}