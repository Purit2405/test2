package com.purit.apptest.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.purit.apptest.R
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.data.SessionManager // เพิ่มตัวจัดการ Session
import com.purit.apptest.databinding.ProductDetailBinding
import com.purit.apptest.models.ProductItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DrinkDetailFragment : Fragment(R.layout.product_detail) {

    private var _binding: ProductDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager // ประกาศ SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ProductDetailBinding.bind(view)

        // เตรียมตัวจัดการ Session (เพื่อดึง Token)
        sessionManager = SessionManager(requireContext())

        // 1. ดึงข้อมูลสินค้า
        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("product_data", ProductItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<ProductItem>("product_data")
        }

        if (product != null) {
            displayProductDetails(product)
        } else {
            Toast.makeText(context, "ไม่พบข้อมูลสินค้า", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayProductDetails(product: ProductItem) {
        binding.apply {
            detailName.text = product.name
            detailPrice.text = "${product.price.toInt()} ฿"
            detailDescription.text = product.description ?: "ไม่มีคำอธิบาย"
            drinkType.text = product.category?.name ?: ""

            Glide.with(requireContext())
                .load(product.image)
                .placeholder(R.drawable.iced_latte)
                .error(R.drawable.iced_latte)
                .into(detailImage)

            if (product.redeemable && product.points_required > 0) {
                layoutRedeemAvailable.visibility = View.VISIBLE
                tvRedeemUnavailable.visibility = View.GONE
                detailPointsRequired.text = "${product.points_required} Points"
                btnRedeemNow.setOnClickListener { showConfirmDialog(product) }
            } else {
                layoutRedeemAvailable.visibility = View.GONE
                tvRedeemUnavailable.visibility = View.VISIBLE
            }
        }
    }

    private fun showConfirmDialog(product: ProductItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("ยืนยันการแลก")
            .setMessage("คุณต้องการใช้ ${product.points_required} แต้มเพื่อแลกสินค้าชิ้นนี้ใช่หรือไม่?")
            .setPositiveButton("ตกลง") { _, _ -> redeemApi(product.id) }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    /**
     * แก้ไขการเรียก API: แนบ Bearer Token เข้าไปใน Header
     */
    private fun redeemApi(productId: Int) {
        // 1. ดึง Token จาก Session
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(context, "กรุณาเข้าสู่ระบบใหม่", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. สร้าง Auth Header (ต้องเป็นคำว่า Bearer นำหน้า)
        val authHeader = "Bearer $token"

        // 3. ส่งข้อมูล 2 ค่า (token และ productId) ตามที่ระบุไว้ใน ApiService
        RetrofitClient.instance.redeemProduct(authHeader, productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "แลกสินค้าสำเร็จ!", Toast.LENGTH_SHORT).show()
                    // อาจจะส่งกลับไปหน้าประวัติ หรือหน้าหลัก
                } else if (response.code() == 401) {
                    Toast.makeText(context, "เซสชันหมดอายุ กรุณา Login ใหม่", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "แต้มไม่เพียงพอ หรือระบบขัดข้อง", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "เชื่อมต่อล้มเหลว: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}