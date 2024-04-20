package it.unipi.dii.fingerprintingapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.os.Handler
import android.os.Looper

class WifiScanner(private val context: Context) {
    private var wifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val _wifiScanResults = MutableLiveData<List<ScanResult>>()
    val wifiScanResults: LiveData<List<ScanResult>> = _wifiScanResults

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == intent.action) {
                _wifiScanResults.value = wifiManager.scanResults
            }
        }
    }

    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
    }

    fun startScan(): Boolean {
        if (checkLocationPermission()) {
            wifiManager.startScan()
            return true
        } else {
            return false
        }
    }
    fun toggleWifi() {
        // Disattiva il Wi-Fi
        wifiManager.isWifiEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({
            // Riattiva il Wi-Fi
            wifiManager.isWifiEnabled = true
        }, 1000) // Attendi un secondo prima di riattivare il Wi-Fi
    }

    fun startScanDelayed() {
        Handler(Looper.getMainLooper()).postDelayed({
            // Avvia la scansione dopo che il Wi-Fi è stato riabilitato
            if (checkLocationPermission()) {
                wifiManager.startScan()
            }
        }, 1000) // Attendi un secondo dopo che il Wi-Fi è stato riattivato
    }
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(wifiScanReceiver)
    }
}
