package com.purit.apptest.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.purit.apptest.R
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.auth.LoginActivity
import com.purit.apptest.data.SessionManager
import com.purit.apptest.models.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var tvName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var btnWebsite: Button
    private lateinit var btnPrivacy: Button
    private lateinit var btnLogout: Button

    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. ผูก View (ID ต้องตรงกับใน fragment_profile.xml)
        tvName = view.findViewById(R.id.tvName)
        tvPhone = view.findViewById(R.id.tvPhone)
        btnWebsite = view.findViewById(R.id.btnWebsite)
        btnPrivacy = view.findViewById(R.id.btnPrivacy)
        btnLogout = view.findViewById(R.id.btnLogout)

        sessionManager = SessionManager(requireContext())

        // 2. ตรวจสอบสถานะ Login
        if (!sessionManager.isLoggedIn()) {
            performLogout()
            return
        }

        // 3. โหลดข้อมูลโปรไฟล์จาก API
        loadUserProfile()

        // 4. ตั้งค่าปุ่มต่างๆ
        btnWebsite.setOnClickListener {
            openUrl("https://www.yourwebsite.com")
        }

        btnPrivacy.setOnClickListener {
            openUrl("https://www.yourwebsite.com/privacy")
        }

        btnLogout.setOnClickListener {
            performLogout()
        }
    }

    /**
     * 👤 วิธีดึงชื่อและเบอร์แบบเดียวกับ PointFragment
     */
    private fun loadUserProfile() {
        val token = sessionManager.fetchAuthToken() ?: return
        val authHeader = "Bearer $token"

        RetrofitClient.instance
            .getUserProfile(authHeader)
            .enqueue(object : Callback<UserProfileResponse> {

                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val user = response.body()!!.data
                        // แสดงผลข้อมูลที่ดึงมาได้
                        tvName.text = user.name
                        tvPhone.text = user.phone ?: "ไม่ระบุเบอร์"
                    } else if (response.code() == 401) {
                        // Token หมดอายุ หรือไม่ถูกต้อง
                        performLogout()
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "โหลดข้อมูลโปรไฟล์ไม่สำเร็จ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    /**
     * 🌐 ฟังก์ชันเปิดลิงก์ภายนอก
     */
    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "ไม่พบแอปพลิเคชันเพื่อเปิดลิงก์", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 🚪 วิธี Logout แบบล้างค่าทั้งหมด (SharedPrefs + Session)
     */
    private fun performLogout() {
        // ล้างค่าใน SessionManager
        sessionManager.logout()

        // ล้างค่าใน SharedPreferences (USER_PREFS) ตามที่คุณต้องการ
        val prefs = requireActivity()
            .getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // ย้ายไปหน้า Login และเคลียร์หน้าเดิมทิ้ง
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        Toast.makeText(requireContext(), "ออกจากระบบเรียบร้อย", Toast.LENGTH_SHORT).show()
    }
}