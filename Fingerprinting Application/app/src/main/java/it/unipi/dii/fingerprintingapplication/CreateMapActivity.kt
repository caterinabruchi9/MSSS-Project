package it.unipi.dii.fingerprintingapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
            val mapId = UUID.randomUUID().toString()

            val mapInfo = MapInfo(rooms, buildingName, mapId)
            RetrofitClient.service.createMap(mapInfo).enqueue(object : Callback<MapResponse> {
                override fun onResponse(call: Call<MapResponse>, response: Response<MapResponse>) {
                    if (response.isSuccessful) {
                        // Handle success
                    } else {
                        // Handle failure
                    }
                }

                override fun onFailure(call: Call<MapResponse>, t: Throwable) {
                    // Handle error
                }
            })
        }
    }

    fun generateMapId(): String {
        return UUID.randomUUID().toString()
    }
}
