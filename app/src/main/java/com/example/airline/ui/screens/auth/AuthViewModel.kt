package com.example.airline.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airline.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: String)  : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signIn(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success(role) }
                .onFailure { _uiState.value = AuthUiState.Error(friendlyMessage(it)) }
        }
    }

    /**
     * Registers the user then immediately signs them in so they land on the
     * dashboard with a valid token — no "please log in now" second step.
     */
    fun signUp(email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signUp(email, password)
                .onSuccess {
                    repository.signIn(email, password)
                        .onSuccess { _uiState.value = AuthUiState.Success(role) }
                        .onFailure { _uiState.value = AuthUiState.Error(friendlyMessage(it)) }
                }
                .onFailure { _uiState.value = AuthUiState.Error(friendlyMessage(it)) }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun friendlyMessage(t: Throwable): String =
        when {
            t.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                "Cannot reach server. Check your connection."
            t.message?.contains("401") == true -> "Invalid email or password."
            t.message?.contains("409") == true -> "An account with this email already exists."
            else -> t.message ?: "Something went wrong. Please try again."
        }
}
