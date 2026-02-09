package com.purit.apptest.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purit.apptest.R
import com.purit.apptest.adapters.PointHistoryAdapter
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.data.SessionManager
import com.purit.apptest.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.purit.apptest.fragments.PointFragment

class PointFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvBalance: TextView
    private lateinit var rvHistory: RecyclerView
    private lateinit var sessionManager: SessionManager
    private var historyList = mutableListOf<PointTransaction>()
    private lateinit var adapter: PointHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_point, container, false)

        initViews(view)

        val token = sessionManager.getToken()
        if (!token.isNullOrEmpty()) {
            val authHeader = "Bearer $token"
            loadAllData(authHeader)
        } else {
            Toast.makeText(context, "กรุณาเข้าสู่ระบบใหม่", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun initViews(view: View) {
        sessionManager = SessionManager(requireContext())
        tvName = view.findViewById(R.id.textView_name)
        tvBalance = view.findViewById(R.id.text_points)
        rvHistory = view.findViewById(R.id.recycler_history)
        val btnRedeem = view.findViewById<Button>(R.id.btnRedeem)

        rvHistory.layoutManager = LinearLayoutManager(context)
        adapter = PointHistoryAdapter(historyList)
        rvHistory.adapter = adapter

        btnRedeem.setOnClickListener {
            // ป้องกัน Error หากยังไม่มี PromotionFragment ให้เช็คชื่อ Class ให้ตรง
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PromotionFragment())
                    .addToBackStack(null).commit()
            } catch (e: Exception) {
                Toast.makeText(context, "หน้าแลกแต้มกำลังพัฒนา", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAllData(token: String) {
        // 1. ดึงชื่อและเบอร์
        RetrofitClient.instance.getUserProfile(token).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    tvName.text = "${user?.name} ID ${user?.phone}"
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {}
        })

        // 2. ดึงยอดแต้มคงเหลือ
        RetrofitClient.instance.getWallet(token).enqueue(object : Callback<WalletResponse> {
            override fun onResponse(call: Call<WalletResponse>, response: Response<WalletResponse>) {
                if (response.isSuccessful) {
                    val balance = response.body()?.data?.balance ?: 0
                    tvBalance.text = String.format("%,d", balance)
                }
            }
            override fun onFailure(call: Call<WalletResponse>, t: Throwable) {}
        })

        // 3. ดึงประวัติแต้ม (จุดที่เคย Error)
        RetrofitClient.instance.getPointHistory(token).enqueue(object : Callback<PointHistoryResponse> {
            override fun onResponse(call: Call<PointHistoryResponse>, response: Response<PointHistoryResponse>) {
                if (response.isSuccessful) {
                    // ดึงจาก pagination -> transactions
                    val data = response.body()?.data?.pagination?.transactions ?: emptyList()
                    historyList.clear()
                    historyList.addAll(data)
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<PointHistoryResponse>, t: Throwable) {
                Log.e("API", "History Fail: ${t.message}")
            }
        })
    }
}