package com.purit.apptest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.purit.apptest.auth.LoginActivity
import com.purit.apptest.data.SessionManager
import com.purit.apptest.fragment.SearchFragment
import com.purit.apptest.fragments.HomeFragment
import com.purit.apptest.fragments.PointFragment
import com.purit.apptest.fragments.ProfileFragment
import com.purit.apptest.fragments.PromotionFragment

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        bottomNav = findViewById(R.id.bottom_navigation)

        // 1. ตรวจสอบ Login
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // 2. ตั้งค่าหน้าแรกตอนเปิดแอป (แสดง HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // 3. จัดการการคลิกที่ Bottom Navigation Menu
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.nav_promotion -> {
                    loadFragment(PromotionFragment())
                    true
                }
                R.id.nav_point -> {
                    loadFragment(PointFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    /**
     * ฟังก์ชันเปลี่ยนหน้า Fragment
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * เมื่อกดปุ่มย้อนกลับบนมือถือ:
     * ถ้าไม่อยู่หน้า Home ให้เด้งกลับไปหน้า Home ก่อนปิดแอป
     */
    override fun onBackPressed() {
        if (bottomNav.selectedItemId != R.id.nav_home) {
            bottomNav.selectedItemId = R.id.nav_home
        } else {
            super.onBackPressed()
        }
    }
}