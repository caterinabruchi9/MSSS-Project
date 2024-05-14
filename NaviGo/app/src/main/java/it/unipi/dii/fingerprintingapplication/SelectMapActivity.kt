package it.unipi.dii.fingerprintingapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import android.location.Location
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.*

// Extend from VolumeNavigationActivity
class SelectMapActivity : VolumeNavigation() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectmap)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Permission check
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        recyclerView = findViewById(R.id.recycler_view_maps)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchAndDisplayMaps(location)
            } else {
                Toast.makeText(this, "Unable to retrieve current location", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchAndDisplayMaps(location: Location) {
        RetrofitClient.service.getMaps().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val jsonData = response.body()?.string()
                    val nearbyMaps = parseNearbyMaps(jsonData, location)

                    if (nearbyMaps.isEmpty()) {
                        Toast.makeText(this@SelectMapActivity, "No maps within 200 meters", Toast.LENGTH_LONG).show()
                    } else {
                        displayMaps(nearbyMaps)

                        // Collect RecyclerView buttons/views for navigation
                        val viewButtons = (0 until recyclerView.childCount).map { recyclerView.getChildAt(it) as Button }
                        buttons = viewButtons // Add these to the navigable list
                    }
                } else {
                    Toast.makeText(this@SelectMapActivity, "Failed to retrieve maps", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@SelectMapActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun parseNearbyMaps(jsonData: String?, location: Location): List<MapInfoDistance> {
        val jsonObject = JsonParser.parseString(jsonData).asJsonObject
        val mapsArray = jsonObject.getAsJsonArray("maps")

        return mapsArray.mapNotNull { jsonArray ->
            val mapLat = jsonArray.asJsonArray[2].asDouble
            val mapLon = jsonArray.asJsonArray[3].asDouble
            val mapId = jsonArray.asJsonArray[1].asInt
            val distance = calculateDistance(location.latitude, location.longitude, mapLat, mapLon)

            // Check if the map is within 200 meters
            if (distance <= 200.0) {
                MapInfoDistance(jsonArray.asJsonArray[0].asString, mapId, distance)
            } else {
                null // Exclude maps that are further than 30 meters
            }
        }
    }


    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Keep the distance calculation logic
        val earthRadius = 6371.0
        val latDiff = Math.toRadians(lat2 - lat1)
        val lonDiff = Math.toRadians(lon2 - lon1)
        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDiff / 2) * sin(lonDiff / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c * 1000
    }

    private fun displayMaps(maps: List<MapInfoDistance>) {
        val adapter = MapsAdapter(maps.take(4)) { selectedMap ->
            // Handle map selection
            val intent = Intent(this@SelectMapActivity, NavigationActivity::class.java)
            // Pass any necessary data to the NavigationActivity using intent extras
            intent.putExtra("MAP_ID", selectedMap.mapId)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                buttons = (0 until recyclerView.childCount).mapNotNull { recyclerView.getChildAt(it) as? Button }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                buttons = (0 until recyclerView.childCount).mapNotNull { recyclerView.getChildAt(it) as? Button }
            }
        })
    }

}


