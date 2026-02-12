package com.purit.apptest.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purit.apptest.R
import com.purit.apptest.adapters.PromotionAdapter
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.data.SessionManager
import com.purit.apptest.models.PromotionResponse
import com.purit.apptest.models.RedeemResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromotionFragment : Fragment(R.layout.fragment_promotion) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var promotionAdapter: PromotionAdapter
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize
        sessionManager = SessionManager(requireContext())
        recyclerView = view.findViewById(R.id.recycler_promotions)
        progressBar = view.findViewById(R.id.progressBar)

        // 2. Setup Adapter
        promotionAdapter = PromotionAdapter { promotion ->
            showConfirmDialog(promotion)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = promotionAdapter

        // 3. Load Data
        fetchPromotions()
    }

    private fun fetchPromotions() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getPromotions().enqueue(object : Callback<List<PromotionResponse>> {
            override fun onResponse(call: Call<List<PromotionResponse>>, response: Response<List<PromotionResponse>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    promotionAdapter.updateList(response.body() ?: emptyList())
                }
            }

            override fun onFailure(call: Call<List<PromotionResponse>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showConfirmDialog(promo: PromotionResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("ยืนยันการรับสิทธิ์")
            .setMessage("ต้องการใช้สิทธิ์ '${promo.title}' ใช่หรือไม่?")
            .setPositiveButton("ตกลง") { _, _ ->
                processRedeem(promo.id)
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun processRedeem(promoId: Int) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"

        RetrofitClient.instance.redeemPromotion(token, promoId).enqueue(object : Callback<RedeemResponse> {
            override fun onResponse(call: Call<RedeemResponse>, response: Response<RedeemResponse>) {
                if (response.isSuccessful) {
                    val msg = response.body()?.message ?: "แลกรับสิทธิ์สำเร็จ"
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    // อัปเดตรายการใหม่เผื่อมีการจำกัดจำนวน
                    fetchPromotions()
                } else {
                    // ดึง Error Message จาก Backend (ถ้ามี)
                    Toast.makeText(context, "ไม่สามารถรับสิทธิ์ได้ (สิทธิ์เต็มหรือแต้มไม่พอ)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RedeemResponse>, t: Throwable) {
                Toast.makeText(context, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_SHORT).show()
            }
        })
    }
}