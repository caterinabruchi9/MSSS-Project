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
import it.unipi.dii.fingerprintingapplication.R
class MainActivity : AppCompatActivity() {
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var wifiScanner: WifiScanner
    private lateinit var textViewResults: TextView
    private lateinit var editTextIdArea: EditText
    private lateinit var editTextCoordinates: EditText
    private lateinit var buttonScan: Button
    private lateinit var textViewStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiScanner = WifiScanner(this)
        textViewResults = findViewById(R.id.textViewResults)
        textViewStatus = findViewById(R.id.textViewStatus)
        editTextIdArea= findViewById(R.id.editTextIdArea)
        editTextCoordinates = findViewById(R.id.editTextCoordinates)
        buttonScan = findViewById(R.id.buttonScan)

        wifiScanner.wifiScanResults.observe(this, Observer { results ->
            val resultText = results.joinToString("\n") { "${it.SSID}, ${it.level}dBm, ${it.frequency}MHz" }
            textViewResults.text = resultText
            textViewStatus.text = "Scan completato"
            buttonScan.isEnabled = true
        })

        buttonScan.setOnClickListener {
            if (checkLocationPermission()) {
                textViewStatus.text = "Scan in corso..."
                buttonScan.isEnabled = false
               // wifiScanner.startScan()
                wifiScanner.toggleWifi()
                wifiScanner.startScanDelayed()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                textViewStatus.text = "Scan in corso..."
                buttonScan.isEnabled = false
                // Permission was granted, resume the scan.
                wifiScanner.startScan()
            } else {
                // Permission denied, disable the functionality that depends on this permission.
                textViewResults.text = "Permission denied. Cannot perform WiFi scan."
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiScanner.unregisterReceiver() // Unregister receiver to avoid memory leaks
    }
}
