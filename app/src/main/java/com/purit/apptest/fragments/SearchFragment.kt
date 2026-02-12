package com.purit.apptest.fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.flexbox.FlexboxLayout
import com.purit.apptest.R
import com.purit.apptest.adapters.ProductAdapter
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.databinding.FragmentSearchBinding
import com.purit.apptest.models.ProductItem
import com.purit.apptest.models.ProductResponse
import com.purit.apptest.fragments.DrinkDetailFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding

    private val searchHistory = mutableListOf<String>()
    private val hotSearchList = listOf(
        "Latte", "Americano", "Mocha", "Green Tea", "Thai Tea"
    )

    private var allProducts: List<ProductItem> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        setupRecyclerView()
        setupSearch()
        setupHotSearch()
        setupClearHistory()
        fetchProducts()
    }

    // ================= RecyclerView =================

    private fun setupRecyclerView() {
        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            isNestedScrollingEnabled = false
        }
    }

    // ================= Load Products =================

    private fun fetchProducts() {
        RetrofitClient.instance.getProducts()
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    if (isAdded && response.isSuccessful) {
                        allProducts = response.body()?.data ?: emptyList()

                        // แสดงทั้งหมดตอนเปิดหน้า
                        showProducts(allProducts)
                        binding.tvResult.text = "All Products"
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {}
            })
    }

    // ================= Search =================

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                val keyword = binding.etSearch.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    addHistory(keyword)
                    performSearch(keyword)
                }
                true
            } else false
        }
    }

    private fun performSearch(keyword: String) {
        binding.tvResult.text = "Search Result : $keyword"

        val result = allProducts.filter {
            it.name.contains(keyword, true)
        }

        showProducts(result)
    }

    // ================= Show Product =================

    private fun showProducts(products: List<ProductItem>) {
        binding.recyclerViewProducts.adapter =
            ProductAdapter(products) { product ->
                openDetail(product)
            }
    }

    // ================= Open Detail =================

    private fun openDetail(product: ProductItem) {
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

    // ================= Search History =================

    private fun addHistory(keyword: String) {
        if (!searchHistory.contains(keyword)) {
            searchHistory.add(0, keyword)
            renderHistory()
        }
    }

    private fun renderHistory() {
        binding.layoutHistory.removeAllViews()
        searchHistory.forEach {
            binding.layoutHistory.addView(createChip(it) {
                performSearch(it)
            })
        }
    }

    private fun setupClearHistory() {
        binding.btnClearHistory.setOnClickListener {
            searchHistory.clear()
            binding.layoutHistory.removeAllViews()
        }
    }

    // ================= Hot Search =================

    private fun setupHotSearch() {
        binding.layoutHot.removeAllViews()
        hotSearchList.forEach {
            binding.layoutHot.addView(createChip(it) {
                performSearch(it)
            })
        }
    }

    // ================= Chip =================

    private fun createChip(text: String, onClick: () -> Unit): View {
        val tv = TextView(requireContext())
        tv.text = text
        tv.setPadding(24, 12, 24, 12)
        tv.setTextColor(resources.getColor(android.R.color.black, null))
        tv.setBackgroundResource(R.drawable.chip_bg)
        tv.setOnClickListener { onClick() }

        val params = FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 8, 8, 8)
        tv.layoutParams = params

        return tv
    }
}
