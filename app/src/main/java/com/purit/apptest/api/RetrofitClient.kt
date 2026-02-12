package com.purit.apptest.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ❗ ไม่ต้อง private เพื่อให้ใช้ภายใน object ได้
    const val BASE_URL = "http://172.16.200.33:8000/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // ✅ helper สำหรับรูป
    fun imageUrl(path: String?): String {
        return if (path.isNullOrEmpty()) {
            ""
        } else {
            BASE_URL + "storage/" + path
        }
    }
}
