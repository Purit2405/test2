package com.purit.apptest.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    // ใช้ applicationContext เพื่อป้องกัน Memory Leak และแอปเด้ง
    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences("apptest_prefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String, expiresAt: Long) {
        val editor = prefs.edit()
        editor.putString("auth_token", token)
        editor.putLong("expires_at", expiresAt)
        editor.putBoolean("is_logged_in", true)
        editor.apply() // ใช้ apply() เพื่อบันทึกแบบ Background
    }

    fun getToken(): String? = prefs.getString("auth_token", null)

    fun isLoggedIn(): Boolean {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val expiresAt = prefs.getLong("expires_at", 0L)

        // เช็กทั้งสถานะ Login และวันหมดอายุ (ระบบ 1 ปีของคุณ)
        return isLoggedIn && System.currentTimeMillis() < expiresAt
    }

    // เพิ่มฟังก์ชัน Logout เพื่อเคลียร์ข้อมูล
    fun logout() {
        prefs.edit().clear().apply()
    }
}