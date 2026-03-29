package com.example.airline.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.remote.CityItem
import com.example.airline.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminCitiesViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _cities = MutableStateFlow<List<CityItem>>(emptyList())
    val cities: StateFlow<List<CityItem>> = _cities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init { fetchCities() }

    fun fetchCities() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCities()
                .onSuccess  { _cities.value = it }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun addCity(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createCity(name)
                .onSuccess  { fetchCities() }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateCity(id: Int, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateCity(id, name)
                .onSuccess  { fetchCities() }
                .onFailure  { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteCity(id: Int) {
        viewModelScope.launch {
            repository.deleteCity(id)
                .onSuccess { fetchCities() }
                .onFailure { _errorMessage.value = it.message }
        }
    }

    fun clearError() { _errorMessage.value = null }
}
