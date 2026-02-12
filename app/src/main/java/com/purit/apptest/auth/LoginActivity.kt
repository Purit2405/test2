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

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            startMainActivity()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {

        binding.ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.btnCreate.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivTogglePassword.setImageResource(R.drawable.ic_hide)
        } else {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivTogglePassword.setImageResource(R.drawable.ic_show)
        }

        isPasswordVisible = !isPasswordVisible
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

        val loginParams = HashMap<String, Any>()
        loginParams["email"] = email
        loginParams["password"] = password
        loginParams["remember_me"] = rememberMe

        RetrofitClient.instance.login(loginParams)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "เข้าสู่ระบบ"

                    if (response.isSuccessful && response.body() != null) {

                        val loginRes = response.body()!!

                        if (loginRes.success && loginRes.data != null) {

                            val token = loginRes.data.token

                            val expiryMillis = if (rememberMe) {
                                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)
                            } else {
                                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)
                            }

                            sessionManager.saveAuthToken(token, expiryMillis)

                            Toast.makeText(
                                this@LoginActivity,
                                "เข้าสู่ระบบสำเร็จ",
                                Toast.LENGTH_SHORT
                            ).show()

                            startMainActivity()

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                loginRes.message ?: "เข้าสู่ระบบไม่สำเร็จ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "อีเมลหรือรหัสผ่านไม่ถูกต้อง",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "เข้าสู่ระบบ"

                    Toast.makeText(
                        this@LoginActivity,
                        "เชื่อมต่อขัดข้อง: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
