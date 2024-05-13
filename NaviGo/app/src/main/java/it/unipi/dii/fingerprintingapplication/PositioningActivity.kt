package it.unipi.dii.fingerprintingapplication

import Fingerprint
import Sample
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import android.widget.EditText
import android.widget.Toast
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonArray
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class PositioningActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var count: Int = 0;
    private lateinit var textViewPosition: TextView
    private lateinit var buttonCalculatePosition: Button
    private lateinit var wifiScanner: WifiScanner
    private var serverFingerprints: List<Sample> = emptyList()  // Memorizza i fingerprint come una lista di Sample
    private lateinit var allBssids: Set<String>

    private val handler = android.os.Handler()
    private val updateRunnable = object : Runnable {
        override fun run() {
            performScanAndCalculatePosition()
            handler.postDelayed(this, 100) // Esegui ogni 100 ms
        }
    }


    private var positionInfoList: List<DirectionInfo> = emptyList()
    private lateinit var textViewFingerprintDetails: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positioning)

        textViewPosition = findViewById(R.id.textViewPosition)
        textViewFingerprintDetails = findViewById(R.id.textViewFingerprintDetails)
        buttonCalculatePosition = findViewById(R.id.buttonCalculatePosition)
        wifiScanner = WifiScanner(this)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)



        val mapId = intent.getIntExtra("MAP_ID", 0)
        fetchFingerprints(mapId)
        fetchPositionInformation(mapId)

        buttonCalculatePosition.setOnClickListener {
            handler.post(updateRunnable)
        }

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity, 0, gravity.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic, 0, geomagnetic.size)
        }
        updateOrientationAngles()
    }
    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implement if needed
    }

    private fun measureAzimuth(): Float {
        var azimut=  Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        azimut = if (azimut < 0) azimut + 360 else azimut
        return azimut

    }

    private fun performScanAndCalculatePosition() {
        count++;
        buttonCalculatePosition.isActivated = false // Disabilita il bottone
        wifiScanner.wifiScanResults.removeObservers(this)  // Rimuovi gli osservatori precedenti
        wifiScanner.startScan()  // Avvia la scansione WiFi
        observeScanResults()     // Aggiungi un nuovo osservatore
    }


    private fun observeScanResults() {
        wifiScanner.wifiScanResults.observe(this, Observer { scanResults ->
            val currentFingerprints = scanResults.map {
                Fingerprint(it.SSID, it.BSSID, it.frequency, it.level)
            }
            val currentSample = Sample(0, 0, currentFingerprints.toMutableList())
            val nearestSample = currentSample.findNearestSample(serverFingerprints, allBssids)
            val azimuthMeasured = measureAzimuth()

            val matchedInfo = positionInfoList.find {
                Math.abs(it.azimuth - azimuthMeasured) < it.threshold &&
                        it.zone == nearestSample.first.first &&
                        it.sample == nearestSample.first.second
            }

            textViewPosition.text = "${azimuthMeasured}\n Nearest Position: Zone: ${nearestSample.first.first}, Sample: ${nearestSample.first.second}\n" +
                    "Info: ${matchedInfo?.info ?: "No matching info found"}"
        })
    }




    private fun fetchPositionInformation(mapId: Int) {
        RetrofitClient.service.getPositionInformation(mapId).enqueue(object : Callback<PositionResponse> {
            override fun onResponse(call: Call<PositionResponse>, response: Response<PositionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    positionInfoList = responseBody.directions.map { list ->
                        DirectionInfo(
                            zone = (list[0] as Double).toInt(),
                            sample = (list[1] as Double).toInt(),
                            azimuth = list[2] as Double,
                            threshold = list[3] as Int,
                            info = list[4] as String
                        )
                    }
                    val mapDetails = responseBody.map
                    val mapId = (mapDetails[0] as Double).toInt()
                    val buildingName = mapDetails[1] as String
                    val rooms = (mapDetails[2] as Double).toInt()
                    val latitude = mapDetails[3] as Double
                    val longitude = mapDetails[4] as Double

                    println("Position information loaded: ${positionInfoList.size} entries")
                } else {
                    println("Failed to fetch position information: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PositionResponse>, t: Throwable) {
                println( "Error fetching position information: ${t.message}")
            }
        })
    }




    private fun fetchFingerprints(mapId: Int) {
        RetrofitClient.service.getFingerprintsForMap(mapId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val json = JsonParser.parseString(responseBody.string()).asJsonObject
                        val fingerprintsJson = json.getAsJsonArray("fingerprints")
                        serverFingerprints = convertToSamples(fingerprintsJson)
                        allBssids = serverFingerprints.flatMap { it.fingerprints.map { fp -> fp.bssid } }.toSet()
                        textViewPosition.text = "Fingerprints loaded: ${serverFingerprints.size}"
                    }
                } else {
                    textViewPosition.text = "Failed to fetch data: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                textViewPosition.text = "Error: ${t.message}"
            }
        })
    }




    private fun convertToSamples(fingerprintsJson: JsonArray): List<Sample> {
        val samples = mutableMapOf<Pair<Int, Int>, MutableList<Fingerprint>>()

        fingerprintsJson.forEach { element ->
            val array = element.asJsonArray
            val ssid = array[0].asString
            val bssid = array[1].asString
            val frequency = array[2].asInt
            val rss = array[3].asInt
            val zone = array[4].asInt
            val sample = array[5].asInt

            val fingerprint = Fingerprint(ssid, bssid, frequency, rss)
            val key = Pair(zone, sample)
            samples.getOrPut(key) { mutableListOf() }.add(fingerprint)
        }

        return samples.map { Sample(it.key.first, it.key.second, it.value) }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.also { mag ->
            sensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_UI)
        }
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
        sensorManager.unregisterListener(this)
        wifiScanner.unregisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        sensorManager.unregisterListener(this)
        wifiScanner.unregisterReceiver()  // Pulisci per evitare memory leaks
    }
}

