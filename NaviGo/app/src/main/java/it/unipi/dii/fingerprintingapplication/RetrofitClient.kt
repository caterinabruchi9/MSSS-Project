package it.unipi.dii.fingerprintingapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val baseUrlFormat = "https://%s.ngrok-free.app/"
    var subdomain = "81e0-131-114-81-61"
    private var retrofit: Retrofit = buildRetrofitClient()

    val service: ApiService
        get() = retrofit.create(ApiService::class.java)

    private fun buildRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(String.format(baseUrlFormat, subdomain))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun updateSubdomain(newSubdomain: String) { //method to update the server address if needed
        subdomain = newSubdomain
        retrofit = buildRetrofitClient()
    }
}
