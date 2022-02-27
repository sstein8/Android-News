package com.example.project_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText



class MainActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var mapButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Title of home page is Android News
        supportActionBar?.title = "Android News"

        //what the user typed into the searchbar
        searchInput = findViewById(R.id.search_input)
        searchButton = findViewById(R.id.search_button)
        searchButton.isEnabled = false

        //Button to navigate to map
        mapButton = findViewById(R.id.view_map_button)

        //Behavior for search button
        searchButton.setOnClickListener{
            var searchInput: String = searchInput.text.toString()
            Log.d("Category",searchInput)
            val searchIntent: Intent = Intent(this, SourcesActivity::class.java)
            searchIntent.putExtra("SearchInput", searchInput)

            // "Executes" our Intent to start a new Activity
            startActivity(searchIntent)

        }
        searchInput.addTextChangedListener(textWatcher)
        //Behavior for map button
        mapButton.setOnClickListener{
            val mapIntent: Intent = Intent(this, MapsActivity::class.java)
            // "Executes" our Intent to start a new Activity
            startActivity(mapIntent)

        }
    }

    // A TextWatcher's functions will be called everytime the user types a character.
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        // We can use any of the three functions -- here, we just use `onTextChanged` -- the goal
        // is the enable the login button only if there is text in both the username & password fields.
        override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // Kotlin shorthand for username.getText().toString()
            // .toString() is needed because getText() returns an Editable (basically a char array).
            val searchTerm: String = searchInput.text.toString()
            val enableButton: Boolean = searchTerm.isNotBlank()

            // Kotlin shorthand for login.setEnabled(enableButton)
            searchButton.isEnabled = enableButton
        }

        override fun afterTextChanged(p0: Editable?) {}

    }


}