package com.facundoamoros.rickandmorty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facundoamoros.rickandmorty.model.Character
import com.facundoamoros.rickandmorty.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel para manejar los datos de los personajes y el estado de la UI
class CharacterViewModel : ViewModel() {

    // Lista de personajes
    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters

    // Mensaje de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Estado de carga
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchCharacters()
    }

    private fun fetchCharacters() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getCharacters()
                _characters.value = response.results
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to fetch characters: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
