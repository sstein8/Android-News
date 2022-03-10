package com.example.project_1

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.project_1.databinding.ActivityMapsBinding
import org.jetbrains.anko.doAsync

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var mapRecyclerView: RecyclerView
    private lateinit var mapRecyclerBackground: TextView
    private lateinit var mapResultsTitle: TextView
    private lateinit var mapProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "News by Location"
        //This needs to be set to invisible until location is found
        mapRecyclerView = findViewById(R.id.MapRecyclerView)
        mapProgressBar = findViewById(R.id.mapProgressBar)

        //Initialize shared prefs


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("Map", "Function Start")
        val preferences = getSharedPreferences("data-persistence-file", Context.MODE_PRIVATE)
        val apiKey = getString(R.string.NEWS_API_KEY)
        val sourcesManager = SourcesManager()
        mMap = googleMap
        mapRecyclerView = findViewById(R.id.MapRecyclerView)
        mapRecyclerBackground = findViewById(R.id.mapRecyclerBackground)
        mapResultsTitle = findViewById(R.id.mapResultsTitle)
        val savedLat = preferences.getString("SAVED_LAT", "")!!
        val savedLon = preferences.getString("SAVED_LON", "")!!
        val savedAdminArea = preferences.getString("SAVED_ADMIN_AREA", "")!!
        val savedAddress = preferences.getString("SAVED_ADDR","")!!
        //Set the white background and title to be invisible until the sources load
        mapRecyclerBackground.visibility = View.INVISIBLE
        mapResultsTitle.visibility = View.INVISIBLE


        Log.d("SavedLat", "hi")
        Log.d("SavedLon", savedLon)

        //If a prev location was set, set the pin there
        if(savedLat != "" && savedLon != ""){
            Log.d("Saved", "In here")
            Log.d("SavedLat", savedLat)
            Log.d("SavedLon", savedLon)
            val coords: LatLng = LatLng(savedLat.toDouble(), savedLon.toDouble())
            val marker = MarkerOptions()
                .position(coords)
                .title(savedAddress)
            mMap.addMarker(marker)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 10.0f))

            doAsync{
                //List of sources
                val mapSourcesList: List<Source> = try {
                    //Retrieve sources using API call
                    sourcesManager.getMapSources(savedAdminArea, apiKey)
                } catch(exception: Exception) {
                    Log.e("MapsActivity", "Retrieving Sources failed", exception)
                    listOf<Source>()
                }

                runOnUiThread {
                    if (mapSourcesList.isNotEmpty()) {

                        val mapSourceAdapter = MapSourceAdapter(mapSourcesList)
                        mapRecyclerView.adapter = mapSourceAdapter
                        //The LinearLayoutManager is used to set scroll direction (default is horizontal)
                        val horizLayoutManager = LinearLayoutManager(this@MapsActivity)
                        horizLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                        mapRecyclerView.layoutManager = horizLayoutManager
                        mapRecyclerBackground.visibility = View.VISIBLE
                        mapResultsTitle.text = "Results for $savedAdminArea"
                        mapResultsTitle.visibility = View.VISIBLE
                    }
                    else {
                        Toast.makeText(this@MapsActivity, "Failed to retrieve sources!", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(38.5, -98.0), 1.0f))

        mMap.setOnMapLongClickListener { coords: LatLng ->
            Log.d("Map", "onLongClickListener")
            mMap.clear()
            mapProgressBar.visibility = View.VISIBLE
            doAsync {
                Log.d("Map", "doAsync1")
                val geocoder: Geocoder = Geocoder(this@MapsActivity)
                val results: List<Address> = try {
                    geocoder.getFromLocation(
                        coords.latitude,
                        coords.longitude,
                        10
                    )


                } catch(exception: Exception) {
                    Log.e("MapsActivity", "Geocoding failed!", exception)
                    listOf()
                }
                mapProgressBar.visibility = View.INVISIBLE

                runOnUiThread {
                    Log.d("Map", "UI Thread")
                    if (results.isNotEmpty()) {
                        val firstResult = results[0]
                        val addressLine = firstResult.getAddressLine(0)
                        var adminArea = firstResult.adminArea

                        val marker = MarkerOptions()
                            .position(coords)
                            .title(addressLine)
                        //Now we have the address, need to make call to API
                        //Retrieve news sources based on location
                        doAsync{
                            //List of sources
                            val mapSourcesList: List<Source> = try {
                                //Retrieve sources using API call
                                sourcesManager.getMapSources(adminArea, apiKey)
                            } catch(exception: Exception) {
                                Log.e("MapsActivity", "Retrieving Sources failed", exception)
                                listOf<Source>()
                            }

                            runOnUiThread {
                                if (mapSourcesList.isNotEmpty()) {

                                    val mapSourceAdapter = MapSourceAdapter(mapSourcesList)
                                    mapRecyclerView.adapter = mapSourceAdapter
                                    //The LinearLayoutManager is used to set scroll direction (default is horizontal)
                                    val horizLayoutManager = LinearLayoutManager(this@MapsActivity)
                                    horizLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                                    mapRecyclerView.layoutManager = horizLayoutManager
                                    mapRecyclerBackground.visibility = View.VISIBLE
                                    mapResultsTitle.text = "Results for $adminArea"
                                    mapResultsTitle.visibility = View.VISIBLE


                                }
                                else {
                                    Toast.makeText(this@MapsActivity, "Failed to retrieve sources!", Toast.LENGTH_LONG).show()
                                }
                            }

                        }

                        mMap.addMarker(marker)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 10.0f))
                        preferences.edit().putString("SAVED_ADMIN_AREA", adminArea.toString()).apply()
                        preferences.edit().putString("SAVED_LAT", coords.latitude.toString()).apply()
                        preferences.edit().putString("SAVED_LON", coords.longitude.toString()).apply()
                        preferences.edit().putString("SAVED_ADDR", addressLine).apply()
                    }
                    else {
                        Toast.makeText(this@MapsActivity, "No results found!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}