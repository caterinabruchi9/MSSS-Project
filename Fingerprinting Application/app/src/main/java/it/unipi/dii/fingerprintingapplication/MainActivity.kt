package it.unipi.dii.fingerprintingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonCreateNewMap = findViewById<Button>(R.id.buttonCreateNewMap)
        buttonCreateNewMap.setOnClickListener {


            val intent = Intent(this, CreateMapActivity::class.java)
            intent.putExtra("mode", "new")
            startActivity(intent)
        }

        val buttonUseExistingMap = findViewById<Button>(R.id.buttonUseExistingMap)
        buttonUseExistingMap.setOnClickListener {
            fetchMapsAndShowDialog()
        }
    }

    private fun fetchMapsAndShowDialog() {
        RetrofitClient.service.getMaps().enqueue(object : Callback<MapListResponse> {
            override fun onResponse(call: Call<MapListResponse>, response: Response<MapListResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val maps = response.body()!!.maps
                    showMapSelectionDialog(maps)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to retrieve maps", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MapListResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun showMapSelectionDialog(maps: List<MapInfo>) {
        val mapNames = maps.map { it.buildingName }.toTypedArray()
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Select a Map")
        dialogBuilder.setItems(mapNames, DialogInterface.OnClickListener { dialog, which ->
            val selectedMap = maps[which]
            goToScanActivity(selectedMap.mapId)
        })
        val mapDialog = dialogBuilder.create()
        mapDialog.show()
    }

    private fun goToScanActivity(mapId: Int) {
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra("MAP_ID", mapId)
        startActivity(intent)
    }
}

