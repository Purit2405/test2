package com.purit.apptest.auth // ย้ายมาอยู่ใน package auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.purit.apptest.MainActivity
import com.purit.apptest.R
import com.purit.apptest.data.SessionManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val sessionManager = SessionManager(this)

        // จำลองโหลด 2 วินาที (2000ms)
        Handler(Looper.getMainLooper()).postDelayed({
            checkStatus(sessionManager)
        }, 2000)
    }

    private fun checkStatus(sessionManager: SessionManager) {
        if (sessionManager.isLoggedIn()) {
            // ถ้าเคย Login และยังไม่หมดอายุ (ระบบ 1 ปี) -> เข้าหน้าหลัก
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // ถ้ายังไม่ Login หรือหมดอายุแล้ว -> ไปหน้า Login
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}