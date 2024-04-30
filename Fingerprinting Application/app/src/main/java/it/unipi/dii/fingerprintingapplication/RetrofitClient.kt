package it.unipi.dii.fingerprintingapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val baseUrlFormat = "https://%s.ngrok-free.app/"
    var subdomain = "713e-131-114-63-2"
    private var retrofit: Retrofit = buildRetrofitClient()

    val service: ApiService
        get() = retrofit.create(ApiService::class.java)

    private fun buildRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(String.format(baseUrlFormat, subdomain))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun updateSubdomain(newSubdomain: String) {
        subdomain = newSubdomain
        retrofit = buildRetrofitClient()  // Ricostruisci il client con il nuovo URL base
    }
}
