package com.purit.apptest.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.purit.apptest.MainActivity
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.data.SessionManager
import com.purit.apptest.databinding.ActivityRegisterBinding
import com.purit.apptest.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnRegister.setOnClickListener {
            performRegister()
        }

        binding.tvBackToLogin.setOnClickListener {
            finish() // กลับไปหน้า Login
        }
    }

    private fun performRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val passwordConfirm = binding.etPasswordConfirm.text.toString().trim()

        // 1. Validation เบื้องต้น
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != passwordConfirm) {
            Toast.makeText(this, "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "กำลังสมัคร..."

        // 2. เตรียม Data (ชื่อ Key ต้องตรงกับ Laravel Validation)
        val params = mutableMapOf<String, Any>()
        params["name"] = name
        params["email"] = email
        params["phone"] = phone
        params["password"] = password
        params["password_confirmation"] = passwordConfirm // สำคัญมากสำหรับ Laravel

        // 3. ยิง API
        RetrofitClient.instance.register(params).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "สมัครสมาชิก"

                if (response.isSuccessful && response.body() != null) {
                    val res = response.body()!!
                    if (res.success) {
                        // สมัครเสร็จแล้ว บันทึก Token และเข้าหน้าหลักทันที
                        val token = res.data?.token ?: ""
                        // สมัครใหม่ให้ Default ไว้ที่ 30 วัน (หรือจะเปลี่ยนตามใจชอบ)
                        val expiry = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)
                        sessionManager.saveAuthToken(token, expiry)

                        Toast.makeText(this@RegisterActivity, "ยินดีต้อนรับคุณ $name", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@RegisterActivity, res.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // กรณี Email หรือ Phone ซ้ำ Laravel จะส่ง 422 กลับมา
                    Toast.makeText(this@RegisterActivity, "อีเมลหรือเบอร์โทรนี้ถูกใช้งานแล้ว", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "สมัครสมาชิก"
                Toast.makeText(this@RegisterActivity, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}