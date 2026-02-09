package com.purit.apptest.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.purit.apptest.R
import com.purit.apptest.adapters.BannerAdapter
import com.purit.apptest.adapters.CategoryAdapter
import com.purit.apptest.adapters.ProductAdapter
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.databinding.FragmentHomeBinding
import com.purit.apptest.models.BannerResponse
import com.purit.apptest.models.CategoryResponse
import com.purit.apptest.models.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ระบบ Auto Slide สำหรับ Banner
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        _binding?.let {
            val totalItems = it.promoViewPager.adapter?.itemCount ?: 0
            if (totalItems > 1) {
                val nextItem = (it.promoViewPager.currentItem + 1) % totalItems
                it.promoViewPager.currentItem = nextItem
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupBannerConfig()
        setupClickListeners()

        // ดึงข้อมูลทั้งหมดจาก API
        refreshData()
    }

    private fun setupRecyclerViews() {
        // หมวดหมู่ (แนวนอน)
        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        // รายการสินค้า (ตาราง 2 คอลัมน์)
        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            isNestedScrollingEnabled = false // เพื่อให้ทำงานร่วมกับ NestedScrollView ใน XML ได้ลื่นไหล
        }
    }

    private fun setupClickListeners() {
        binding.btnbell.setOnClickListener {
            navigateToFragment(NewsFragment())
        }
    }

    private fun refreshData() {
        fetchBanners()
        fetchCategories()
        fetchProducts()
    }

    private fun fetchCategories() {
        RetrofitClient.instance.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (isAdded && response.isSuccessful) {
                    val categoryList = response.body()?.data ?: listOf()
                    binding.recyclerViewCategories.adapter = CategoryAdapter(categoryList) { category ->
                        val fragment = CategoryFragment().apply {
                            arguments = Bundle().apply {
                                putInt("category_id", category.id)
                                putString("category_name", category.name)
                            }
                        }
                        navigateToFragment(fragment)
                    }
                }
            }
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.e("API_ERROR", "Category Fail: ${t.message}")
            }
        })
    }

    private fun fetchProducts() {
        RetrofitClient.instance.getProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (isAdded && response.isSuccessful) {
                    val productList = response.body()?.data ?: listOf()

                    // เชื่อมต่อการคลิกจาก Adapter ไปยังหน้า DrinkDetailFragment
                    binding.recyclerViewProducts.adapter = ProductAdapter(productList) { product ->
                        val fragment = DrinkDetailFragment().apply {
                            arguments = Bundle().apply {
                                // ส่ง Object ProductItem ไปทั้งก้อน (ต้องเป็น Parcelable)
                                putParcelable("product_data", product)
                            }
                        }
                        navigateToFragment(fragment)
                    }
                }
            }
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.e("API_ERROR", "Product Fail: ${t.message}")
            }
        })
    }

    private fun fetchBanners() {
        RetrofitClient.instance.getBanners().enqueue(object : Callback<BannerResponse> {
            override fun onResponse(call: Call<BannerResponse>, response: Response<BannerResponse>) {
                if (isAdded && response.isSuccessful) {
                    binding.promoViewPager.adapter = BannerAdapter(response.body()?.data ?: listOf())
                }
            }
            override fun onFailure(call: Call<BannerResponse>, t: Throwable) {}
        })
    }

    private fun setupBannerConfig() {
        binding.promoViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000) // สไลด์ทุก 3 วินาที
            }
        })
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            // ลบ setCustomAnimations ออกไปเลย
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sliderHandler.removeCallbacks(sliderRunnable) // หยุด Slider เมื่อเปลี่ยนหน้าเพื่อประหยัด RAM
        _binding = null
    }
}