package com.purit.apptest.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.purit.apptest.R
import com.purit.apptest.adapters.PointHistoryAdapter
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.data.SessionManager
import com.purit.apptest.models.PointHistoryResponse
import com.purit.apptest.models.PointResponse
import com.purit.apptest.models.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PointFragment : Fragment(R.layout.fragment_point) {

    private lateinit var tvName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvPoints: TextView
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var btnRedeem: MaterialButton

    private lateinit var sessionManager: SessionManager
    private lateinit var historyAdapter: PointHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. ผูก View (ตรวจสอบ ID ใน XML ให้ตรงกัน)
        tvName = view.findViewById(R.id.textView_name)
        tvPhone = view.findViewById(R.id.textView_phone)
        tvPoints = view.findViewById(R.id.text_points)
        recyclerHistory = view.findViewById(R.id.recycler_history)
        btnRedeem = view.findViewById(R.id.btnRedeem)

        sessionManager = SessionManager(requireContext())

        // 2. ตั้งค่า RecyclerView
        historyAdapter = PointHistoryAdapter(mutableListOf())
        recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        recyclerHistory.adapter = historyAdapter

        // 3. คลิกปุ่ม Exchange (Exchange -> PromotionFragment)
        btnRedeem.setOnClickListener {
            val promotionFragment = PromotionFragment()

            // ใช้ parentFragmentManager และตรวจสอบ ID fragment_container ใน activity_main.xml
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, promotionFragment)
                .addToBackStack(null) // เพื่อให้กด Back กลับมาหน้า Point ได้
                .commit()
        }

        // 4. โหลดข้อมูลจาก API
        if (sessionManager.isLoggedIn()) {
            loadUserProfile()
            loadWallet()
            loadHistory()
        } else {
            Toast.makeText(requireContext(), "กรุณาเข้าสู่ระบบ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserProfile() {
        val token = sessionManager.fetchAuthToken() ?: return
        val authHeader = "Bearer $token"

        RetrofitClient.instance.getUserProfile(authHeader)
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    if (isAdded && response.isSuccessful) {
                        val user = response.body()?.data
                        tvName.text = user?.name ?: "ไม่พบชื่อ"
                        tvPhone.text = user?.phone ?: "ไม่พบเบอร์"
                    }
                }
                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {}
            })
    }

    private fun loadWallet() {
        val token = sessionManager.fetchAuthToken() ?: return
        val authHeader = "Bearer $token"

        RetrofitClient.instance.getWalletBalance(authHeader)
            .enqueue(object : Callback<PointResponse> {
                override fun onResponse(call: Call<PointResponse>, response: Response<PointResponse>) {
                    if (isAdded && response.isSuccessful) {
                        val balance = response.body()?.data?.balance ?: 0
                        tvPoints.text = String.format("%,d", balance)
                    }
                }
                override fun onFailure(call: Call<PointResponse>, t: Throwable) {}
            })
    }

    private fun loadHistory() {
        val token = sessionManager.fetchAuthToken() ?: return
        val authHeader = "Bearer $token"

        RetrofitClient.instance.getPointHistory(authHeader)
            .enqueue(object : Callback<PointHistoryResponse> {
                override fun onResponse(call: Call<PointHistoryResponse>, response: Response<PointHistoryResponse>) {
                    if (isAdded && response.isSuccessful) {
                        val historyList = response.body()?.data?.data ?: emptyList()
                        historyAdapter.updateData(historyList)
                    }
                }
                override fun onFailure(call: Call<PointHistoryResponse>, t: Throwable) {}
            })
    }
}