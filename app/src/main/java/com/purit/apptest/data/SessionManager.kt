package com.purit.apptest.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    // ใช้ applicationContext ป้องกัน memory leak
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    companion object {
        private const val PREF_NAME = "apptest_prefs"

        private const val KEY_TOKEN = "auth_token"
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val KEY_LOGGED_IN = "is_logged_in"
    }

    /**
     * 🔐 บันทึก Token ตอน Login สำเร็จ
     * @param token Bearer token จาก Laravel
     * @param expiresAt เวลาหมดอายุ (millis)
     */
    fun saveAuthToken(token: String, expiresAt: Long) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    /**
     * ✅ ดึง Token ออกมาใช้กับ API
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * (alias) ใช้แทนกันได้
     */
    fun getToken(): String? {
        return fetchAuthToken()
    }

    /**
     * 🔎 เช็คว่ายัง Login อยู่ไหม + Token ยังไม่หมดอายุ
     */
    fun isLoggedIn(): Boolean {
        val loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false)
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)

        return loggedIn &&
                fetchAuthToken() != null &&
                System.currentTimeMillis() < expiresAt
    }

    /**
     * ⏰ เวลาหมดอายุ Token
     */
    fun getExpiresAt(): Long {
        return prefs.getLong(KEY_EXPIRES_AT, 0L)
    }

    /**
     * 🚪 Logout – ล้าง Session ทั้งหมด
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}