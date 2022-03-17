package com.example.project_1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.w3c.dom.Text

class TopHeadlinesActivity : AppCompatActivity() {
    private lateinit var topHeadlinesRecyclerView: RecyclerView
    private lateinit var selectedCategory: String
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var pageNumText: TextView
    private lateinit var progressBarHeadlines: ProgressBar

    val sourcesManager = SourcesManager()
    var maxNumPages: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_headlines)
        //Set the title of the screen
        supportActionBar?.title = "Top Headlines"

        //Initialize shared prefs
        val preferences = getSharedPreferences("data-persistence-file", Context.MODE_PRIVATE)

        //Get reference to the recycler view
        topHeadlinesRecyclerView = findViewById(R.id.topHeadlinesRecyclerView)

        //Get reference to the buttons
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)
        //Page num textview
        pageNumText = findViewById(R.id.pageNum)
        progressBarHeadlines = findViewById(R.id.progressBarHeadlines)

        var currPageNum = 1


        //get the categories from the string array in strings.xml file
        val categoriesList = resources.getStringArray(R.array.categories)
        val topHeadlinesSpinner: Spinner = findViewById(R.id.topHeadlinesSpinner)

        var spinnerIndex:Int = 0

        if (topHeadlinesSpinner != null) {
            val spinner_adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, categoriesList
            )
            topHeadlinesSpinner.adapter = spinner_adapter

            val saved = preferences.getInt("SAVED_CATEGORY",spinnerIndex)

            if(saved != null){
                topHeadlinesSpinner.setSelection(saved)
            }
            else{
                topHeadlinesSpinner.setSelection(0)
            }

            //Listen for the user to select a category
            topHeadlinesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    currPageNum = 1
                    topHeadlinesSpinner.setSelection(position)
                    spinnerIndex = position
                    selectedCategory = parent.getItemAtPosition(position).toString().lowercase()
                    preferences.edit().putInt("SAVED_CATEGORY", spinnerIndex).apply()
                    loadPage(selectedCategory, currPageNum)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            nextButton.setOnClickListener {

                //if the current page is less than the last one, you can move forward
                if (currPageNum < maxNumPages) {
                    //enable button
                    currPageNum += 1 //increase page number (next page)
                    //show next page
                    loadPage(selectedCategory, currPageNum)

                } else {
                    //disable button, cant go forward anymore
                    nextButton.isEnabled = false
                }
            }

            prevButton.setOnClickListener {

                if (currPageNum > 1) {
                    //enable button
                    currPageNum -= 1 //decrease page number
                    loadPage(selectedCategory, currPageNum)

                } else {
                    //disable button, cant go back anymore
                    nextButton.isEnabled = false
                }
            }


        }
    }

    fun loadPage(selectedCategory: String, currPageNum: Int){
        // Use our sourcesManager to get info from API.
        // If there is network connection issues, the catch-block will fire and we'll show the user an error message.
        val apiKey = getString(R.string.NEWS_API_KEY)
        progressBarHeadlines.visibility = View.VISIBLE
        doAsync {
            val headlinesList: List<Source> = try {
                sourcesManager.retrieveTopHeadlines(selectedCategory, apiKey, currPageNum)

            } catch (exception: Exception) {
                Log.e("TopHeadlines", "Retrieving Sources failed", exception)
                listOf<Source>()
            }
            progressBarHeadlines.visibility = View.INVISIBLE

            //Function to get the number of pages to set up
            maxNumPages = sourcesManager.totalNumPages

            runOnUiThread {
                if (headlinesList.isNotEmpty()) {
                    //Update the page num on the bottom of the screen
                    val text: String = "$currPageNum / $maxNumPages"
                    pageNumText.text = text
                    val headlines_adapter = MapSourceAdapter(headlinesList)
                    topHeadlinesRecyclerView.adapter = headlines_adapter
                    //The LinearLayoutManager is used to set scroll direction (default is horizontal)
                    topHeadlinesRecyclerView.layoutManager = LinearLayoutManager(this@TopHeadlinesActivity)

                }
                else {
                    Toast.makeText(
                        this@TopHeadlinesActivity,
                        "Failed to retrieve news!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }
}


