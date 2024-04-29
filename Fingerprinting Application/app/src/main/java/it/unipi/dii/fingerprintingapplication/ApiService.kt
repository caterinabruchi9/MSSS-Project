package it.unipi.dii.fingerprintingapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

interface ApiService {
    @POST("maps")
    fun createMap(@Body mapInfo: MapInfo): Call<MapResponse>

    @GET("maps/list")
    fun getMaps(): Call<MapListResponse>

}

data class MapInfo(
    @SerializedName("rooms")val rooms: Int,
    @SerializedName("building-name") val buildingName: String,
    @SerializedName("map-id")val mapId: Int
)

data class MapResponse(
    val status: Int,
    val message: String?
)

data class MapListResponse(
    val maps: List<MapInfo>
)
