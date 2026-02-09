package com.purit.apptest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.purit.apptest.adapters.NewsAdapter
import com.purit.apptest.api.RetrofitClient
import com.purit.apptest.databinding.FragmentNewsBinding
import com.purit.apptest.models.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()
        fetchNews()
    }

    private fun setupRecyclerView() {
        binding.rvAllNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun fetchNews() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (!isAdded) return
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val newsList = response.body()!!.data
                    // ส่งแค่ List เข้าไป ไม่ต้องมี Action การคลิก
                    binding.rvAllNews.adapter = NewsAdapter(newsList)
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                if (!isAdded) return
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}