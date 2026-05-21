package com.example.bomberosapp.ui

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository = UserRepository(),
    private val prefs: SharedPreferences? = null
) : ViewModel() {

    var loginState by mutableStateOf<LoginUIState>(LoginUIState.Idle)
        private set

    init {
        checkSavedUser()
    }

    private fun checkSavedUser() {
        prefs?.getString("saved_user", null)?.let {
            // Se podría implementar un auto-login aquí si se guarda el token
        }
    }

    fun getSavedUser(): String = prefs?.getString("saved_user", "") ?: ""

    fun login(usuario: String, pass: String, rememberMe: Boolean, onLoginSuccess: () -> Unit) {
        if (usuario.isBlank() || pass.isBlank()) {
            loginState = LoginUIState.Error("Complete todos los campos")
            return
        }

        loginState = LoginUIState.Loading
        viewModelScope.launch {
            val success = repository.login(usuario, pass)
            if (success) {
                if (rememberMe) {
                    prefs?.edit()?.putString("saved_user", usuario)?.apply()
                } else {
                    prefs?.edit()?.remove("saved_user")?.apply()
                }
                loginState = LoginUIState.Success
                onLoginSuccess()
            } else {
                loginState = LoginUIState.Error("Credenciales Inválidas o Error de Conexión")
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
