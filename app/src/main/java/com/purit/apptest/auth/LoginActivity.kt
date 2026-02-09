package com.purit.apptest.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.purit.apptest.MainActivity
import com.purit.apptest.R
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.data.SessionManager
import com.purit.apptest.databinding.ActivityLoginBinding
import com.purit.apptest.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. ตั้งค่า ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 2. เช็คว่าถ้า Login ค้างไว้อยู่แล้ว (และยังไม่หมดอายุ) ให้ไปหน้าหลักเลย
        if (sessionManager.isLoggedIn()) {
            startMainActivity()
        }

        setupListeners()
    }

    private fun setupListeners() {
        // ปุ่มสลับการมองเห็นรหัสผ่าน
        binding.ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // ปุ่มเข้าสู่ระบบ
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // ไปหน้าสมัครสมาชิก (ID: btnCreate)
        binding.btnCreate.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // >>> เชื่อมไปหน้าลืมรหัสผ่าน (ID: tvForgotPassword) <<<
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivTogglePassword.setImageResource(R.drawable.ic_hide)
        } else {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivTogglePassword.setImageResource(R.drawable.ic_show)
        }
        isPasswordVisible = !isPasswordVisible
        // ให้เคอร์เซอร์อยู่ท้ายข้อความเสมอหลังเปลี่ยน InputType
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val rememberMe = binding.rememberMeSwitch.isChecked

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "กรุณากรอกอีเมลและรหัสผ่าน", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "กำลังตรวจสอบ..."

        val loginParams = mutableMapOf<String, Any>()
        loginParams["email"] = email
        loginParams["password"] = password
        loginParams["remember_me"] = rememberMe

        RetrofitClient.instance.login(loginParams).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "เข้าสู่ระบบ"

                if (response.isSuccessful && response.body() != null) {
                    val loginRes = response.body()!!

                    if (loginRes.success) {
                        // บันทึก Session: ถ้าเลือก 'จดจำฉัน' จะอยู่ได้ 1 ปี (365 วัน)
                        val expiryMillis = if (rememberMe) {
                            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)
                        } else {
                            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)
                        }

                        val token = loginRes.data?.token ?: ""
                        sessionManager.saveAuthToken(token, expiryMillis)

                        Toast.makeText(applicationContext, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show()
                        startMainActivity()
                    } else {
                        Toast.makeText(this@LoginActivity, loginRes.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "อีเมลหรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "เข้าสู่ระบบ"
                Toast.makeText(this@LoginActivity, "เชื่อมต่อไม่ได้: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // ป้องกันการกดย้อนกลับมาหน้า Login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}