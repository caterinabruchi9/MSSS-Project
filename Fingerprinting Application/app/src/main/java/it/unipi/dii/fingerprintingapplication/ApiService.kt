package it.unipi.dii.fingerprintingapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.http.GET

interface ApiService {
    @POST("maps")
    fun createMap(@Body mapInfo: MapInfo): Call<MapResponse>

    @GET("maps/list")
    fun getMaps(): Call<ResponseBody>

    @POST("fingerprint")
    fun sendFingerprint(@Body fingerprintData: FingerprintData): Call<FingerprintResponse>

}

data class MapInfo(
    @SerializedName("rooms")val rooms: Int,
    @SerializedName("building-name") val buildingName: String,
    @SerializedName("map-id")val mapId: Int
)

data class SimpleMapInfo(
    @SerializedName("building-name") val buildingName: String,
    @SerializedName("map-id")val mapId: Int
)

data class MapResponse(
    val status: Int,
    val message: String?
)

data class FingerprintResponse(
    val status: Int,
    val message: String?
)

data class FingerprintData(
    @SerializedName("ssid") val ssid: String,
    @SerializedName("RSS")  val RSS: Int,
    @SerializedName("bssid") val bssid: String,
    @SerializedName("map-id") val mapId: Int,
    @SerializedName("frequency") val frequency: Int,
    @SerializedName("zone") val zone: Int,
    @SerializedName("sample") val sample: Int
)

