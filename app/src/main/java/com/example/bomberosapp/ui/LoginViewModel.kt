package com.example.bomberosapp.ui

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.UserRole
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

    fun login(usuario: String, pass: String, rememberMe: Boolean, onLoginSuccess: (UserRole) -> Unit) {
        if (usuario.isBlank() || pass.isBlank()) {
            loginState = LoginUIState.Error("Complete todos los campos")
            return
        }

        loginState = LoginUIState.Loading
        viewModelScope.launch {
            val role = repository.login(usuario, pass)
            if (role != UserRole.NONE) {
                if (rememberMe) {
                    prefs?.edit()?.putString("saved_user", usuario)?.apply()
                    prefs?.edit()?.putString("user_role", role.name)?.apply()
                } else {
                    prefs?.edit()?.remove("saved_user")?.apply()
                    prefs?.edit()?.remove("user_role")?.apply()
                }
                loginState = LoginUIState.Success(role)
                onLoginSuccess(role)
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
    data class Success(val role: UserRole) : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}
