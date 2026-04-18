package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    var loginState by mutableStateOf<LoginUIState>(LoginUIState.Idle)
        private set

    fun login(usuario: String, pass: String, onLoginSuccess: () -> Unit) {
        val codigo = usuario.toIntOrNull()
        if (codigo == null) {
            loginState = LoginUIState.Error("Usuario inválido")
            return
        }

        loginState = LoginUIState.Loading
        viewModelScope.launch {
            val success = repository.login(codigo, pass)
            if (success) {
                loginState = LoginUIState.Success
                onLoginSuccess()
            } else {
                loginState = LoginUIState.Error("Credenciales Inválidas")
            }
        }
    }

    fun resetState() {
        loginState = LoginUIState.Idle
    }
}

sealed class LoginUIState {
    object Idle : LoginUIState()
    object Loading : LoginUIState()
    object Success : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}
