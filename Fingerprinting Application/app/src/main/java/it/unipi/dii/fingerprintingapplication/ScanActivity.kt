package it.unipi.dii.fingerprintingapplication

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.hardware.SensorEvent
import android.hardware.SensorEventListener


// Main activity class for handling the UI and initiating Wi-Fi scans.
class ScanActivity : AppCompatActivity(), SensorEventListener {
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
    private lateinit var buttonSendFingerprint: Button
    private lateinit var textViewStatus: TextView
    private var mapId: Int = 0

    private lateinit var sensorManager: SensorManager
    private var magnetometer: Sensor? = null
    private var azimutInDegrees=0.0
    private var accelerometer: Sensor? = null

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    // Called when the activity is starting.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the user interface layout for this Activity.
        setContentView(R.layout.activity_scan)
        mapId = intent.getIntExtra("MAP_ID", 0)
        // Initialize the Wi-Fi scanner and UI components.
        wifiScanner = WifiScanner(this)
        textViewResults = findViewById(R.id.textViewResults)
        textViewStatus = findViewById(R.id.textViewStatus)
        editTextIdArea= findViewById(R.id.editTextIdArea)
        editTextCoordinates = findViewById(R.id.editTextCoordinates)
        buttonScan = findViewById(R.id.buttonScan)
        buttonSendFingerprint = findViewById(R.id.buttonSendFingerprint)
        buttonSendFingerprint.isEnabled = false

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)



        // Observes changes in the Wi-Fi scan results and updates the UI accordingly.
        wifiScanner.wifiScanResults.observe(this, Observer { results ->
            val sortedResults = results.sortedWith(compareBy({ it.SSID }, { it.BSSID }))
            val resultText = sortedResults.joinToString("\n") { result ->
                "SSID: ${result.SSID}, " +
                        "BSSID: ${result.BSSID}, " +
                        "Frequency: ${result.frequency}MHz, " +
                        "Level: ${result.level}dBm, " + '\n'
            }
            textViewResults.text = "${azimutInDegrees}\n" + resultText
            textViewStatus.text = "Scan completed"
            buttonScan.isEnabled = true
        })

        // Sets up the button click listener to start Wi-Fi scanning.
        buttonScan.setOnClickListener {
            buttonSendFingerprint.isEnabled = true
            if (checkLocationPermission()) {
                textViewStatus.text = "Scan in progress..."
                buttonScan.isEnabled = false
                // Triggers the Wi-Fi to toggle and start scanning.
                //wifiScanner.toggleWifi()
                //wifiScanner.startScanDelayed()
                azimutInDegrees = Math.toDegrees(orientationAngles[0].toDouble()) // Converti in gradi
                if (azimutInDegrees < 0)
                    azimutInDegrees += 360
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
        buttonSendFingerprint.setOnClickListener {
            sendFingerprintData()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        // orientationAngles[0] contiene l'azimut in radianti
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Qui puoi gestire i cambiamenti di accuratezza del sensore se necessario
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
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    private fun sendFingerprintData() {
        val areaId = editTextIdArea.text.toString()
        val coordinates = editTextCoordinates.text.toString()

        if (areaId.isBlank() || coordinates.isBlank()) {
            Toast.makeText(this, "Please enter valid area ID and coordinates", Toast.LENGTH_SHORT).show()
            return
        }



        wifiScanner.wifiScanResults.value?.forEach { scanResult ->
            val fingerprintData = FingerprintData(
                ssid = scanResult.SSID,
                RSS = scanResult.level,
                bssid = scanResult.BSSID,
                mapId = mapId,
                frequency = scanResult.frequency,
                zone = editTextIdArea.text.toString().toInt(),
                sample = editTextCoordinates.text.toString().toInt(),
                azimut = azimutInDegrees
            )

            RetrofitClient.service.sendFingerprint(fingerprintData).enqueue(object : Callback<FingerprintResponse> {
                override fun onResponse(call: Call<FingerprintResponse>, response: Response<FingerprintResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Fingerprint sent successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Failed to send fingerprint: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<FingerprintResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error sending fingerprint: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
    }


