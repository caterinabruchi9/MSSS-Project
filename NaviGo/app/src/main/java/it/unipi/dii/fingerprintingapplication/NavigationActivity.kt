package it.unipi.dii.fingerprintingapplication

import Fingerprint
import Sample
import android.os.Bundle
import android.widget.Button
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
import android.speech.tts.TextToSpeech
import android.widget.EditText
import android.widget.Toast
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NavigationActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var tts: TextToSpeech
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private lateinit var buttonGetInformation: Button
    private val handler = android.os.Handler()
    private val updateRunnable = object : Runnable {
        override fun run() {
            performScanAndCalculatePosition()
            handler.postDelayed(this, 100) // Execute every 100 ms
        }
    }

    private lateinit var wifiScanner: WifiScanner
    private var serverFingerprints: List<Sample> = emptyList()
    private lateinit var allBssids: Set<String>
    private var positionInfoList: List<DirectionInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        buttonGetInformation = findViewById(R.id.buttonGetInformation)

        val mapId = intent.getIntExtra("MAP_ID", 0)
        fetchFingerprints(mapId)
        fetchPositionInformation(mapId)

        buttonGetInformation.setOnClickListener {
            speakPositionInformation()
        }

        wifiScanner = WifiScanner(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
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
        wifiScanner.unregisterReceiver()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity, 0, gravity.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic, 0, geomagnetic.size)
        }
        updateOrientationAngles()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implement if needed
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

    private fun measureAzimuth(): Float {
        var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        azimuth = if (azimuth < 0) azimuth + 360 else azimuth
        return azimuth
    }

    private fun performScanAndCalculatePosition() {
        wifiScanner.wifiScanResults.removeObservers(this)
        wifiScanner.startScan()
        observeScanResults()
    }

    private fun observeScanResults() {
        wifiScanner.wifiScanResults.observe(this, { scanResults ->
            val currentFingerprints = scanResults.map {
                Fingerprint(it.SSID, it.BSSID, it.frequency, it.level)
            }
            val currentSample = Sample(0, 0, currentFingerprints.toMutableList())
            val nearestSample = currentSample.findNearestSample(serverFingerprints, allBssids)
            val azimuthMeasured = measureAzimuth()

            val treshold= if(nearestSample.first.first==4 || nearestSample.first.first==6) 45 else 90

            val matchedInfo = positionInfoList.find {
                Math.abs(it.azimuth - azimuthMeasured) < treshold &&
                        it.zone == nearestSample.first.first &&
                        it.sample == nearestSample.first.second
            }

            val positionText = "Azimuth: $azimuthMeasured\n" +
                    "Nearest Position: Zone: ${nearestSample.first.first}, Sample: ${nearestSample.first.second}\n" +
                    "Info: ${matchedInfo?.info ?: "No matching info found"}"

            buttonGetInformation.text = positionText
            speakPositionInformationIfNeeded(matchedInfo)
        })
    }

    private fun speakPositionInformationIfNeeded(matchedInfo: DirectionInfo?) {
        if (matchedInfo != null) {
            speak(matchedInfo.info)
        }
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
                            info = list[3] as String
                        )
                    }
                    println("Position information loaded: ${positionInfoList.size} entries")
                } else {
                    println("Failed to fetch position information: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PositionResponse>, t: Throwable) {
                println("Error fetching position information: ${t.message}")
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
                    }
                } else {
                    println("Failed to fetch data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Error: ${t.message}")
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

    private fun speakPositionInformation() {
        val positionText = buttonGetInformation.text.toString()
        speak(positionText)
    }

    private fun speak(text: String) {
        val tts = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                val locale = Locale.getDefault()
                tts.language = locale
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }
}
