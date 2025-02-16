package com.example.submisionstoryapp.ui.maps

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.submisionstoryapp.R
import com.example.submisionstoryapp.databinding.ActivityMapsBinding
import com.example.submisionstoryapp.data.preference.UserPref
import com.example.submisionstoryapp.data.preference.dataStore
import com.example.submisionstoryapp.ui.helper.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.example.submisionstoryapp.data.Result

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()

    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private lateinit var userPref: UserPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UserPref
        userPref = UserPref.getInstance(applicationContext.dataStore)

        // Observe user session and fetch stories
        observeUserSession()

        // Setup Google Maps
        setupMap()
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observeUserSession() {
        lifecycleScope.launchWhenStarted {
            userPref.getSession().collect { userModel ->
                val token = userModel.token
                if (token.isNotEmpty()) {
                    mapsViewModel.getStoriesWithLocation("Bearer $token")
                    observeStoryResponse()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun observeStoryResponse() {
        mapsViewModel.storiesWithLocation.observe(this) { storyResponse ->
            if (!storyResponse.error && storyResponse.listStory.isNotEmpty()) {
                storyResponse.listStory.forEach { story ->
                    val latLng = LatLng(story.lat, story.lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(story.description)
                    )
                    boundsBuilder.include(latLng)
                }
                val bounds: LatLngBounds = boundsBuilder.build()
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        300
                    )
                )
            }
        }
    }
}