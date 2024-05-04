package it.unipi.dii.fingerprintingapplication

import CompassActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.EditText
import android.widget.Toast
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MainActivity : AppCompatActivity() {

    private lateinit var editTextServerSubdomain: EditText
    private lateinit var buttonChangeServerAddress: Button
    private lateinit var buttonCompassTest: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonCreateNewMap = findViewById<Button>(R.id.buttonCreateNewMap)
        editTextServerSubdomain = findViewById(R.id.editTextServerAddress)
        buttonChangeServerAddress = findViewById(R.id.buttonChangeServerAddress)
        buttonCreateNewMap.setOnClickListener {


            val intent = Intent(this, CreateMapActivity::class.java)
            intent.putExtra("mode", "new")
            startActivity(intent)
        }

        val buttonUseExistingMap = findViewById<Button>(R.id.buttonUseExistingMap)
        buttonUseExistingMap.setOnClickListener {
            fetchMapsAndShowDialog()
        }
        val buttonTestPositioning = findViewById<Button>(R.id.buttonTestPositioning)
        buttonTestPositioning.setOnClickListener {
            fetchMapsAndShowDialog2()
        }

        buttonChangeServerAddress.setOnClickListener {
            val newSubdomain = editTextServerSubdomain.text.toString()
            if (newSubdomain.isNotBlank()) {
                RetrofitClient.updateSubdomain(newSubdomain)
                Toast.makeText(this, "Server subdomain updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid server subdomain.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonCompassTest.setOnClickListener {
            val intent = Intent(this, CompassActivity::class.java)
            startActivity(intent)
        }

    }



    private fun fetchMapsAndShowDialog() {
        RetrofitClient.service.getMaps().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val jsonData = response.body()?.string()
                    val jsonObject = JsonParser.parseString(jsonData).asJsonObject
                    val mapsArray = jsonObject.getAsJsonArray("maps")
                    val maps = mapsArray.map { jsonArray ->
                        jsonArray.asJsonArray.let {
                            SimpleMapInfo(it[0].asString, it[1].asInt)
                        }
                    }
                    showMapSelectionDialog(maps)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to retrieve maps", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchMapsAndShowDialog2() {
        RetrofitClient.service.getMaps().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val jsonData = response.body()?.string()
                    val jsonObject = JsonParser.parseString(jsonData).asJsonObject
                    val mapsArray = jsonObject.getAsJsonArray("maps")
                    val maps = mapsArray.map { jsonArray ->
                        jsonArray.asJsonArray.let {
                            SimpleMapInfo(it[0].asString, it[1].asInt)
                        }
                    }
                    showMapSelectionDialog2(maps)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to retrieve maps", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun showMapSelectionDialog(maps: List<SimpleMapInfo>) {
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




    private fun showMapSelectionDialog2(maps: List<SimpleMapInfo>) {
        val mapNames = maps.map { it.buildingName }.toTypedArray()
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Select a Map")
        dialogBuilder.setItems(mapNames, DialogInterface.OnClickListener { dialog, which ->
            val selectedMap = maps[which]
            goToPositioningActivity(selectedMap.mapId)
        })
        val mapDialog = dialogBuilder.create()
        mapDialog.show()
    }

    private fun goToScanActivity(mapId: Int) {
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra("MAP_ID", mapId)
        startActivity(intent)
    }

    private fun goToPositioningActivity(mapId: Int) {
        val intent = Intent(this, PositioningActivity::class.java)
        intent.putExtra("MAP_ID", mapId)
        startActivity(intent)
    }
    private fun localizeMe(fingerprintData: List<FingerprintData>, mapId: List<Int>) {


    }
}

