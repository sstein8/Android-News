package com.example.project_1

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.util.Log
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "News by Location"


        //This needs to be set to invisible until location is found
        mapRecyclerView = findViewById(R.id.MapRecyclerView)
        Log.d("Map", "onCreate")

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
        val apiKey = "926eace80c65401ea3413c7582c466f7"
        val sourcesManager = SourcesManager()
        mMap = googleMap
        mapRecyclerView = findViewById(R.id.MapRecyclerView)
        mapRecyclerBackground = findViewById(R.id.mapRecyclerBackground)
        mapResultsTitle = findViewById(R.id.mapResultsTitle)
        //Set the white background and title to be invisible until the sources load
        mapRecyclerBackground.visibility = View.INVISIBLE
        mapResultsTitle.visibility = View.INVISIBLE

        mMap.setOnMapLongClickListener { coords: LatLng ->
            Log.d("Map", "onLongClickListener")
            mMap.clear()

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

                                    //   mapRecyclerView.layoutManager = LinearLayoutManager(this@MapsActivity)

                                }
                                else {
                                    Toast.makeText(this@MapsActivity, "Failed to retrieve sources!", Toast.LENGTH_LONG).show()
                                }
                            }

                        }

                        mMap.addMarker(marker)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 10.0f))
                    }
                    else {
                        Toast.makeText(this@MapsActivity, "No results found!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}