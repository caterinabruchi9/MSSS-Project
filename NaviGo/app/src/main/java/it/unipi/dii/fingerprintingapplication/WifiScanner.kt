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

// Class to manage WiFi scanning
class WifiScanner(private val context: Context) {
    private var wifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val _wifiScanResults = MutableLiveData<List<ScanResult>>()
    val wifiScanResults: LiveData<List<ScanResult>> = _wifiScanResults

    // BroadcastReceiver to handle results of WiFi scans
    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) { 
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == intent.action) {
                // Post the scan results to LiveData to be observed
                _wifiScanResults.postValue(wifiManager.scanResults)
            }
        }
    }

    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
    }

    // Function to start a WiFi scan
    fun startScan(): Boolean {
        if (checkLocationPermission()) {
            wifiManager.startScan()
            return true
        } else {
            return false
        }
    }

    // Function to disable and then re-enable WiFi to force a fresh scan
    // Used to solve initial issues but no more necessary
    fun toggleWifi() {
        wifiManager.isWifiEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({
            wifiManager.isWifiEnabled = true
        }, 1000)  // Wait 1 second before re-enabling WiFi
    }

    // Function to initiate a WiFi scan after a delay following WiFi re-enable
    fun startScanDelayed() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (checkLocationPermission()) {
                wifiManager.startScan()
            }
        }, 1000)  // Wait 1 second after WiFi has been re-enabled
    }

    // Utility function to check if location permissions are granted
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Function to unregister the BroadcastReceiver when no longer needed to avoid memory leaks
    fun unregisterReceiver() {
        context.unregisterReceiver(wifiScanReceiver)
    }
}

