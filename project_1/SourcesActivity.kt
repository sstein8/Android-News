package com.example.project_1

import android.util.Log
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync


class SourcesActivity : AppCompatActivity(){

    private lateinit var RecyclerView: RecyclerView
    private lateinit var selectedCategory: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)

        // Retrieves the data associated with the "LOCATION" key from the Intent used to launch
        // this Activity (the one we created in the MainActivity)
        val searchInput: String = intent.getStringExtra("SearchInput")!!
        Log.d("MainActivity", searchInput)
        // You can supply additional data parameters if that string has any placeholders that need filling.
        supportActionBar?.title = "Search for $searchInput"

        val sourcesManager = SourcesManager()
        val apiKey = "926eace80c65401ea3413c7582c466f7"

        //get the categories from the string array in strings.xml file
        val categoriesList = resources.getStringArray(R.array.categories)

        val spinner: Spinner = findViewById(R.id.spinner)
        if (spinner != null) {
            val spinner_adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, categoriesList
            )
            spinner.adapter = spinner_adapter

            //Set the default to business
            spinner.setSelection(0)
            selectedCategory = categoriesList[0].lowercase()
            //Listen for the user to select a category
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    spinner.setSelection(position)
                    selectedCategory = parent.getItemAtPosition(position).toString().lowercase()
                    Log.d("CategoryInSources", selectedCategory)

                    // Networking needs to be done on a background thread
                    doAsync {
                        // Use our TwitterManager to get Tweets from the Twitter API. If there is network
                        // connection issues, the catch-block will fire and we'll show the user an error message.
                        val sourcesList: List<Source> = try {
                            sourcesManager.retrieveSources(selectedCategory, apiKey)
                        } catch(exception: Exception) {
                            Log.e("SourcesActivity", "Retrieving Sources failed", exception)
                            listOf<Source>()
                        }

                        runOnUiThread {
                            if (sourcesList.isNotEmpty()) {
                                val source_adapter = SourceAdapter(sourcesList)
                                RecyclerView.adapter = source_adapter
                                //The LinearLayoutManager is used to set scroll direction (default is horizontal)
                                RecyclerView.layoutManager = LinearLayoutManager(this@SourcesActivity)
                            } else {
                                Toast.makeText(
                                    this@SourcesActivity,
                                    "Failed to retrieve sources!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }
        }

        //Get reference to the recycler view
        RecyclerView = findViewById(R.id.RecyclerView)







    }












/*
    fun getFakeNews(): List<Source> {
        return listOf(
            Source(
                newsSource = "New York Times",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            ),
            Source(
                newsSource = "New York Post",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            ),
            Source(
                newsSource = "LA Times",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            ),
            Source(
                newsSource = "Chicago Tribune",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            ),
            Source(
                newsSource = "People Magazine",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            ),
            Source(
                newsSource = "US Weekly",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            ),
            Source(
                newsSource = "Glamour",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            ),
            Source(
                newsSource = "GQ",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            ),
            Source(
                newsSource = "Thrasher Magazine",
                newsText = "To celebrate the release of our new album, Unlimited Love, releasing April 1st, you have exclusive access to a limited edition vinyl copy of the album, only available to the Red Hot Chili Peppers top fans on Spotify."
            )

        )
    }

 */



}