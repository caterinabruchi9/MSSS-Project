package it.unipi.dii.fingerprintingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.net.MediaType
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call
import java.io.IOException
import java.util.UUID

class CreateMapActivity : AppCompatActivity(){

    // Late-initialized properties for UI components.
    private lateinit var editTextBuildingName: EditText
    private lateinit var editTextRooms: EditText
    private lateinit var buttonCreateMap: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createmap)

        editTextBuildingName = findViewById(R.id.editTextBuildingName)
        editTextRooms = findViewById(R.id.editTextRooms)
        buttonCreateMap = findViewById(R.id.buttonCreateMap)

        buttonCreateMap.setOnClickListener {
            val buildingName = editTextBuildingName.text.toString()
            val rooms = editTextRooms.text.toString().toInt()
            val mapId = UUID.randomUUID().hashCode()

            val mapInfo = MapInfo(rooms, buildingName, mapId)
            sendMapData(mapInfo)
        }
    }

    private fun sendMapData(mapInfo: MapInfo) {
        RetrofitClient.service.createMap(mapInfo).enqueue(object : Callback<MapResponse> {
            override fun onResponse(call: Call<MapResponse>, response: Response<MapResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    // Controllo dello stato della risposta
                    if (responseBody.status == 200) {
                        // Azione in caso di successo
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Map created successfully! Message: ${responseBody.message}", Toast.LENGTH_LONG).show()
                        }
                        val intent = Intent(this@CreateMapActivity, MainActivity::class.java)
                        intent.putExtra("mode", "new")
                        startActivity(intent)
                    } else {
                        // Azione in caso di risposta non riuscita ma valida
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Failed to create map: ${responseBody.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    // Gestione di una risposta non riuscita, es. 400 o 500 status code
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error creating map: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MapResponse>, t: Throwable) {
                // Gestione di errori di rete o altro
                runOnUiThread {
                    Toast.makeText(applicationContext, "Network error or other issue: ${t.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
