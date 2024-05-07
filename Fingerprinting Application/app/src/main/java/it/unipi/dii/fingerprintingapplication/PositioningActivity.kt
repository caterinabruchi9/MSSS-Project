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

class PositioningActivity : AppCompatActivity() {
    private var count: Int = 0;
    private lateinit var textViewPosition: TextView
    private lateinit var buttonCalculatePosition: Button
    private lateinit var wifiScanner: WifiScanner
    private var serverFingerprints: List<Sample> = emptyList()  // Memorizza i fingerprint come una lista di Sample
    private lateinit var allBssids: Set<String>

    private lateinit var textViewFingerprintDetails: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positioning)

        textViewPosition = findViewById(R.id.textViewPosition)
        textViewFingerprintDetails = findViewById(R.id.textViewFingerprintDetails)
        buttonCalculatePosition = findViewById(R.id.buttonCalculatePosition)
        wifiScanner = WifiScanner(this)



        val mapId = intent.getIntExtra("MAP_ID", 0)
        fetchFingerprints(mapId)

        buttonCalculatePosition.setOnClickListener {
            performScanAndCalculatePosition()
        }
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
            val start = System.nanoTime()
            val nearestSample = currentSample.findNearestSample(serverFingerprints, allBssids)
            val end = System.nanoTime()
            buttonCalculatePosition.isEnabled = true  // Riabilita il bottone
            textViewPosition.text = "Nearest Position: Zone: ${nearestSample.first.first}, Sample: ${nearestSample.first.second}\nDistance: ${nearestSample.second}\nComputation time: ${(end - start) / 1_000_000} ms"
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



    override fun onDestroy() {
        super.onDestroy()
        wifiScanner.unregisterReceiver()  // Pulisci per evitare memory leaks
    }
}

