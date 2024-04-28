package it.unipi.dii.fingerprintingapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("maps")
    fun createMap(@Body mapInfo: MapInfo): Call<MapResponse>
}

data class MapInfo(
    val rooms: Int,
    val buildingName: String,
    val mapId: String
)

data class MapResponse(
    val status: Int,
    val message: String?
)