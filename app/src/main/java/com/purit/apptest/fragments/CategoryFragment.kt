package com.purit.apptest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.purit.apptest.R
import com.purit.apptest.adapters.ProductAdapter
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.databinding.FragmentCategoryBinding
import com.purit.apptest.models.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = arguments?.getInt("category_id") ?: 0
        binding.tvToolbarTitle.text = arguments?.getString("category_name") ?: "Category"

        // ตั้งค่า LayoutManager ให้เป็น 2 คอลัมน์เหมือนหน้า Home
        binding.rvProductsByCategory.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        loadProducts(categoryId)
    }

    private fun loadProducts(categoryId: Int) {
        RetrofitClient.instance.getProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (isAdded && response.isSuccessful) {
                    val allItems = response.body()?.data ?: listOf()

                    // 1. กรองข้อมูลเฉพาะ Category ID ที่เลือก
                    val filtered = allItems.filter { it.category?.id == categoryId }

                    // 2. ส่งทั้งรายการสินค้า และ คำสั่งคลิก (onItemClick) เข้าไปใน Adapter
                    binding.rvProductsByCategory.adapter = ProductAdapter(filtered) { product ->
                        // เมื่อคลิกสินค้า ให้ไปหน้ารายละเอียด
                        val fragment = DrinkDetailFragment().apply {
                            arguments = Bundle().apply {
                                putParcelable("product_data", product)
                            }
                        }

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}