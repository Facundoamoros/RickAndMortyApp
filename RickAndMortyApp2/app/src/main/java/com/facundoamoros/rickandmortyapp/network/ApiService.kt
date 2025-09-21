package com.facundoamoros.rickandmorty.network

import com.facundoamoros.rickandmorty.model.CharacterResponse
import retrofit2.http.GET

// Define API endpoints
interface ApiService {
    // GET request to fetch all characters
    @GET("character")
    suspend fun getCharacters(): CharacterResponse
}
