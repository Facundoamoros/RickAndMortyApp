package com.facundoamoros.rickandmorty.model

// Represents a single character from the API
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String
)

// Represents the API response containing a list of characters
data class CharacterResponse(
    val results: List<Character>
)
