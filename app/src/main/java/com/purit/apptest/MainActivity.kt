package com.purit.apptest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.purit.apptest.auth.LoginActivity
import com.purit.apptest.data.SessionManager
import com.purit.apptest.fragments.HomeFragment // ตรวจสอบ package ของ HomeFragment
import com.purit.apptest.fragments.PointFragment

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        // 1. ตรวจสอบ Login เหมือนเดิม
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // 2. โหลด HomeFragment ขึ้นมาแสดงเป็นหน้าแรก
        if (savedInstanceState == null) {
            loadFragment(PointFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}