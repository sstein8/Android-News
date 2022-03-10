package com.example.project_1

import android.content.Intent
import android.util.Log
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find


class SourcesActivity : AppCompatActivity(){

    private lateinit var sourceRecyclerView: RecyclerView
    private lateinit var selectedCategory: String
    private lateinit var skipSources: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)

        val searchInput: String = intent.getStringExtra("SearchInput")!!
        Log.d("MainActivity", searchInput)
        supportActionBar?.title = "Search for $searchInput"

        val sourcesManager = SourcesManager()
        val apiKey = getString(R.string.NEWS_API_KEY)

        //Get reference to the recycler view
        sourceRecyclerView = findViewById(R.id.sourceRecyclerView)

        //Get reference to skipSources button
        skipSources = findViewById(R.id.skipSourcesButton)

        //get the categories from the string array in strings.xml file
        val categoriesList = resources.getStringArray(R.array.categories)

        progressBar = findViewById(R.id.progressBar)

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
                    progressBar.visibility = View.VISIBLE
                    doAsync {
                        // Use our TwitterManager to get Tweets from the Twitter API. If there is network
                        // connection issues, the catch-block will fire and we'll show the user an error message.
                        val sourcesList: List<Source> = try {
                            sourcesManager.retrieveSources(selectedCategory, searchInput,apiKey)
                        } catch(exception: Exception) {
                            Log.e("SourcesActivity", "Retrieving Sources failed", exception)
                            listOf<Source>()
                        }
                        progressBar.visibility = View.INVISIBLE

                        runOnUiThread {
                            if (sourcesList.isNotEmpty()) {

                                val source_adapter = SourceAdapter(sourcesList)
                                sourceRecyclerView.adapter = source_adapter
                                //The LinearLayoutManager is used to set scroll direction (default is horizontal)
                                sourceRecyclerView.layoutManager = LinearLayoutManager(this@SourcesActivity)
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
            Log.d("MainActivity", "before skipSources")

            //Handle if user clicks skip sources button
            skipSources.setOnClickListener{
                var skipSourcesIntent: Intent = Intent(this, SourceResultsActivity::class.java)
                Log.d("SourcesActivity", "search term: $searchInput")

                skipSourcesIntent.putExtra("SearchInput", searchInput)
                skipSourcesIntent.putExtra("SourceName", "") //no source selected
                skipSourcesIntent.putExtra("SourceID","")
                startActivity(skipSourcesIntent)
            }
        }




    }


}