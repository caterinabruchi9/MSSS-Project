package it.unipi.dii.fingerprintingapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call
import java.util.UUID

class CreateMapActivity : AppCompatActivity(){

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
                    if (responseBody.status == 200) {
                        // Success
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Map created successfully! Message: ${responseBody.message}", Toast.LENGTH_LONG).show()
                        }
                        val intent = Intent(this@CreateMapActivity, TestActivity::class.java)
                        intent.putExtra("mode", "new")
                        startActivity(intent)
                    } else {
                        // Server down or other error
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Failed to create map: ${responseBody.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    // error in the response
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error creating map: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MapResponse>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Network error or other issue: ${t.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
