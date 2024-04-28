package it.unipi.dii.fingerprintingapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer

// Main activity class for handling the UI and initiating Wi-Fi scans.
class ScanActivity : AppCompatActivity() {
    companion object {
        // Request code for location permission request.
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    // Late-initialized properties for UI components.
    private lateinit var wifiScanner: WifiScanner
    private lateinit var textViewResults: TextView
    private lateinit var editTextIdArea: EditText
    private lateinit var editTextCoordinates: EditText
    private lateinit var buttonScan: Button
    private lateinit var textViewStatus: TextView

    // Called when the activity is starting.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the user interface layout for this Activity.
        setContentView(R.layout.activity_scan)

        // Initialize the Wi-Fi scanner and UI components.
        wifiScanner = WifiScanner(this)
        textViewResults = findViewById(R.id.textViewResults)
        textViewStatus = findViewById(R.id.textViewStatus)
        editTextIdArea= findViewById(R.id.editTextIdArea)
        editTextCoordinates = findViewById(R.id.editTextCoordinates)
        buttonScan = findViewById(R.id.buttonScan)

        // Observes changes in the Wi-Fi scan results and updates the UI accordingly.
        wifiScanner.wifiScanResults.observe(this, Observer { results ->
            val sortedResults = results.sortedWith(compareBy({ it.SSID }, { it.BSSID }))
            val resultText = sortedResults.joinToString("\n") { result ->
                "SSID: ${result.SSID}, " +
                        "BSSID: ${result.BSSID}, " +
                        "Frequency: ${result.frequency}MHz, " +
                        "Level: ${result.level}dBm, " + '\n'
            }
            textViewResults.text = resultText
            textViewStatus.text = "Scan completed"
            buttonScan.isEnabled = true
        })

        // Sets up the button click listener to start Wi-Fi scanning.
        buttonScan.setOnClickListener {
            if (checkLocationPermission()) {
                textViewStatus.text = "Scan in progress..."
                buttonScan.isEnabled = false
                // Triggers the Wi-Fi to toggle and start scanning.
                //wifiScanner.toggleWifi()
                //wifiScanner.startScanDelayed()
                wifiScanner.startScan()
            } else {
                // Requests the necessary permissions if not already granted.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    // Checks if the location permission has been granted.
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Callback for the result from requesting permissions. Handles the user's response.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                textViewStatus.text = "Scan in progress..."
                buttonScan.isEnabled = false
                // Starts the scan if permission was granted.
                wifiScanner.startScan()
            } else {
                // Disables scanning functionality if permission was denied.
                textViewResults.text = "Permission denied. Cannot perform WiFi scan."
            }
        }
    }

    // Called when the activity is being destroyed to prevent memory leaks.
    override fun onDestroy() {
        super.onDestroy()
        wifiScanner.unregisterReceiver() // Unregister receiver to avoid memory leaks
    }
}

