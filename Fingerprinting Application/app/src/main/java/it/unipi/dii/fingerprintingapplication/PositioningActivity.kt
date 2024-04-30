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

class PositioningActivity : AppCompatActivity() {

    private lateinit var textViewPosition: TextView
    private lateinit var buttonCalculatePosition: Button
    private lateinit var wifiScanner: WifiScanner
    private var serverFingerprints: List<Sample> = emptyList()  // Memorizza i fingerprint come una lista di Sample

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positioning)

        textViewPosition = findViewById(R.id.textViewPosition)
        buttonCalculatePosition = findViewById(R.id.buttonCalculatePosition)
        wifiScanner = WifiScanner(this)

        val mapId = intent.getIntExtra("MAP_ID", 0)
        fetchFingerprints(mapId)

        buttonCalculatePosition.setOnClickListener {
            performScanAndCalculatePosition()
        }
    }

    private fun performScanAndCalculatePosition() {
        wifiScanner.startScan()  // Avvia la scansione WiFi
        observeScanResults()     // Osserva i risultati della scansione e calcola la posizione
    }

    private fun observeScanResults() {
        wifiScanner.wifiScanResults.observe(this, Observer { scanResults ->
            val currentFingerprints = scanResults.map {
                Fingerprint(it.SSID, it.BSSID, it.frequency, it.level)
            }
            val currentSample = Sample(0, 0, currentFingerprints.toMutableList())
            val nearestSample = currentSample.findNearestSample(serverFingerprints)
            textViewPosition.text = "Nearest Position: Zone: ${nearestSample?.first}, Sample: ${nearestSample?.second}"
        })
    }

    private fun fetchFingerprints(mapId: Int) {
        RetrofitClient.service.getFingerprintsForMap(mapId).enqueue(object : Callback<ResponseBody> {  // Utilizza ResponseBody se necessario decodificare manualmente il JSON
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val json = JsonParser.parseString(responseBody.string()).asJsonObject
                        val fingerprintsJson = json.getAsJsonArray("fingerprints")  // Ottieni l'array di fingerprints
                        val fingerprints = fingerprintsJson.map { it.asJsonArray.toList() }
                        serverFingerprints = convertToSamples(fingerprints as List<List<Any>>)
                        textViewPosition.text = "Fingerprints loaded successfully!"
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

    private fun convertToSamples(fingerprints: List<List<Any>>?): List<Sample> {
        val samples = mutableMapOf<Pair<Int, Int>, MutableList<Fingerprint>>()

        fingerprints?.forEach { rawFp ->
            if (rawFp.size >= 6) {  // Assicurati che ci siano abbastanza elementi nell'array per evitare errori
                val ssid = rawFp[0] as? String ?: ""
                val bssid = rawFp[1] as? String ?: ""
                val frequency = (rawFp[2] as? Number)?.toInt() ?: 0
                val rss = (rawFp[3] as? Number)?.toInt() ?: 0
                val zone = (rawFp[4] as? Number)?.toInt() ?: 0
                val sample = (rawFp[5] as? Number)?.toInt() ?: 0

                val fingerprint = Fingerprint(ssid, bssid, frequency, rss)
                val key = Pair(zone, sample)
                samples.getOrPut(key) { mutableListOf() }.add(fingerprint)
            }
        }

        return samples.map { Sample(it.key.first, it.key.second, it.value) }
    }


    override fun onDestroy() {
        super.onDestroy()
        wifiScanner.unregisterReceiver()  // Pulisci per evitare memory leaks
    }
}

