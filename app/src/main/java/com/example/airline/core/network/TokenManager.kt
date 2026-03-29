package com.example.airline.core.network

import android.content.Context
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class JwtPayload(val id: Int, val email: String, val role: String)

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("airline_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    /** Decodes the stored JWT payload without verifying the signature.
     *  Returns null if no token is stored or the payload is malformed. */
    fun decodePayload(): JwtPayload? {
        val token = getToken() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val decoded = String(
                Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            )
            val json = JSONObject(decoded)
            JwtPayload(
                id    = json.getInt("id"),
                email = json.getString("email"),
                role  = json.getString("role")
            )
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
    }
}
