package com.example.cinemaster.data

import android.content.Context
import java.security.MessageDigest

// Kết quả trả về sau mỗi thao tác xác thực
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val userDao: UserDao,
    context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        const val GUEST_USER_ID = -1

        /** SHA-256 hash — dùng chung cho Repository và AppDatabase seed */
        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }

    val isGuest: Boolean get() = prefs.getBoolean("is_guest", false)
    val isLoggedIn: Boolean get() = prefs.contains("user_id") && !isGuest
    val isAuthenticated: Boolean get() = prefs.contains("user_id")
    val currentUserId: Int get() = prefs.getInt("user_id", GUEST_USER_ID)
    val currentUserName: String get() = prefs.getString("user_name", "Khách") ?: "Khách"

    suspend fun login(email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        val user = userDao.findByEmail(normalizedEmail)
            ?: return AuthResult.Error("Email này chưa được đăng ký.")
        return if (user.passwordHash == hashPassword(password)) {
            saveSession(userId = user.id, name = user.name, isGuest = false)
            AuthResult.Success
        } else {
            AuthResult.Error("Mật khẩu không đúng. Vui lòng thử lại.")
        }
    }

    suspend fun register(name: String, email: String, password: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()
        if (userDao.findByEmail(normalizedEmail) != null)
            return AuthResult.Error("Email này đã được đăng ký rồi.")
        return try {
            val id = userDao.insert(
                User(
                    name = name.trim(),
                    email = normalizedEmail,
                    passwordHash = hashPassword(password)
                )
            )
            saveSession(userId = id.toInt(), name = name.trim(), isGuest = false)
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error("Đăng ký thất bại. Vui lòng thử lại.")
        }
    }

    fun continueAsGuest() {
        saveSession(userId = GUEST_USER_ID, name = "Khách", isGuest = true)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    private fun saveSession(userId: Int, name: String, isGuest: Boolean) {
        prefs.edit()
            .putInt("user_id", userId)
            .putString("user_name", name)
            .putBoolean("is_guest", isGuest)
            .apply()
    }
}
