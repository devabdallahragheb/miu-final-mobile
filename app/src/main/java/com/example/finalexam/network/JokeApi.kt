package com.example.finalexam.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class JokeResponse(
    val type: String?,
    val setup: String?,
    val punchline: String?,
    val joke: String?
)

interface JokeApiService {
    @GET("random_joke")
    suspend fun getRandomJoke(): JokeResponse
}

object JokeApi {
    private const val BASE_URL = "https://official-joke-api.appspot.com/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val service: JokeApiService = retrofit.create(JokeApiService::class.java)
}
