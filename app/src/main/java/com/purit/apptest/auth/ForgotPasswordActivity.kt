package com.purit.apptest.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.databinding.ActivityForgotPasswordBinding
import com.purit.apptest.models.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ปุ่มส่งลิงก์รีเซ็ทรหัสผ่าน (ID จาก XML ของคุณ)
        binding.btnSubmitForgot.setOnClickListener {
            val email = binding.etForgotEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "กรุณากรอกอีเมลของคุณ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSubmitForgot.isEnabled = false
            binding.btnSubmitForgot.text = "กำลังส่ง..."

            val params = mapOf("email" to email)

            // แก้ไข Callback เป็น <BaseResponse> ให้ตรงกับ ApiService
            RetrofitClient.instance.forgotPassword(params).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    binding.btnSubmitForgot.isEnabled = true
                    binding.btnSubmitForgot.text = "ส่งลิงก์รีเซ็ทรหัสผ่าน"

                    if (response.isSuccessful && response.body() != null) {
                        val msg = response.body()!!.message
                        Toast.makeText(this@ForgotPasswordActivity, msg, Toast.LENGTH_LONG).show()
                        finish() // กลับไปหน้า Login
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "ไม่พบอีเมลนี้ในระบบ", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    binding.btnSubmitForgot.isEnabled = true
                    binding.btnSubmitForgot.text = "ส่งลิงก์รีเซ็ทรหัสผ่าน"
                    Toast.makeText(this@ForgotPasswordActivity, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // ปุ่มกลับไปหน้าเข้าสู่ระบบ (ID จาก XML ของคุณ)
        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
    }
}