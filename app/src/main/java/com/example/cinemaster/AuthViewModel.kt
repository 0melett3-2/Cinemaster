package com.example.cinemaster

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinemaster.data.AuthRepository
import com.example.cinemaster.data.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(private val authRepo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        // Validate trước — không cần gọi coroutine nếu input sai
        validateLogin(email, password)?.let { err ->
            _state.value = AuthUiState(error = err)
            return
        }
        viewModelScope.launch {
            _state.value = AuthUiState(loading = true)
            _state.value = when (val result = authRepo.login(email, password)) {
                is AuthResult.Success -> AuthUiState(isAuthenticated = true)
                is AuthResult.Error  -> AuthUiState(error = result.message)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        validateRegister(name, email, password)?.let { err ->
            _state.value = AuthUiState(error = err)
            return
        }
        viewModelScope.launch {
            _state.value = AuthUiState(loading = true)
            _state.value = when (val result = authRepo.register(name, email, password)) {
                is AuthResult.Success -> AuthUiState(isAuthenticated = true)
                is AuthResult.Error  -> AuthUiState(error = result.message)
            }
        }
    }

    fun continueAsGuest() {
        authRepo.continueAsGuest()
        _state.value = AuthUiState(isAuthenticated = true)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    // ── Validation ──────────────────────────────────────────────────────────

    private fun validateLogin(email: String, password: String): String? = when {
        email.isBlank()                                               -> "Vui lòng nhập email."
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()       -> "Địa chỉ email không hợp lệ."
        password.isBlank()                                            -> "Vui lòng nhập mật khẩu."
        else                                                          -> null
    }

    private fun validateRegister(name: String, email: String, password: String): String? = when {
        name.isBlank()                                                -> "Vui lòng nhập tên hiển thị."
        email.isBlank()                                               -> "Vui lòng nhập email."
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()       -> "Địa chỉ email không hợp lệ."
        password.length < 8                                           -> "Mật khẩu phải có ít nhất 8 ký tự."
        else                                                          -> null
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CinemasterApp
                AuthViewModel(app.authRepository)
            }
        }
    }
}
