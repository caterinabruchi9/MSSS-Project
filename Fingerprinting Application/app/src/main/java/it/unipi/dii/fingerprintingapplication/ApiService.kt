package it.unipi.dii.fingerprintingapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

import retrofit2.http.GET

interface ApiService {
    @POST("maps")
    fun createMap(@Body mapInfo: MapInfo): Call<MapResponse>

    @GET("maps/list")
    fun getMaps(): Call<List<MapInfo>>
}

data class MapInfo(
    val rooms: Int,
    val buildingName: String,
    val mapId: Int
)

data class MapResponse(
    val status: Int,
    val message: String?
)