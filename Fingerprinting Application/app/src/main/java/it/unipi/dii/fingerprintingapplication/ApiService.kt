package it.unipi.dii.fingerprintingapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @POST("maps")
    fun createMap(@Body mapInfo: MapInfo): Call<MapResponse>

    @GET("maps/list")
    fun getMaps(): Call<ResponseBody>

    @POST("fingerprint")
    fun sendFingerprint(@Body fingerprintData: FingerprintData): Call<FingerprintResponse>

    @GET("maps/{mapId}")
    fun getFingerprintsForMap(@Path("mapId") mapId: Int): Call<ResponseBody>  // Usa ResponseBody per la decodifica manuale

    @GET("direction/{mapId}")
    fun getPositionInformation(@Path("mapId") mapId: Int): Call<PositionResponse>
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

data class FullFingerprint(
    @SerializedName("ssid") val ssid: String,
    @SerializedName("RSS")  val RSS: Int,
    @SerializedName("bssid") val bssid: String,
    @SerializedName("frequency") val frequency: Int,
    @SerializedName("zone") val zone: Int,
    @SerializedName("sample") val sample: Int
)

data class MapData(
    val fingerprints: List<List<Any>>,
    val map: List<Any>,
    val status: Int
)

data class DirectionInfo(
    @SerializedName("zone") val zone: Int,
    @SerializedName("sample") val sample: Int,
    @SerializedName("azimuth") val azimuth: Double,
    @SerializedName("info") val info: String
)
data class PositionResponse(
    val directions: List<List<Any>>,
    val map: List<Any>,
    val status: Int
)
