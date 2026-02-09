package com.purit.apptest.api

import com.purit.apptest.models.LoginResponse
import com.purit.apptest.models.BaseResponse // เพิ่มบรรทัดนี้
import com.purit.apptest.models.BannerResponse
import com.purit.apptest.models.CategoryResponse
import com.purit.apptest.models.NewsResponse // Import ตัวที่เราสร้างไว้
import com.purit.apptest.models.ProductResponse
import com.purit.apptest.models.PromotionResponse
import com.purit.apptest.models.*

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path // <--- ต้องมีบรรทัดนี้
import retrofit2.http.Header

interface ApiService {
    // แนะนำให้ระบุ Path ให้ชัดเจนตามโครงสร้าง Backend
    @POST("api/auth/login") // ใส่ api/ นำหน้าถ้า backend กำหนดไว้
    fun login(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<LoginResponse>
    // ใช้ LoginResponse ร่วมกันได้เลยเพราะโครงสร้าง JSON เหมือนกันครับ
    @POST("api/auth/forgot-password")
    fun forgotPassword(@Body body: Map<String, String>): Call<BaseResponse>
// หมายเหตุ: สร้าง BaseResponse หรือใช้ LoginResponse (ที่ success/message เหมือนกัน) ก็ได้ครับ
@GET("api/public/banners")
fun getBanners(): Call<BannerResponse>

    @GET("api/public/categories")
    fun getCategories(): Call<CategoryResponse>
    @GET("api/public/news")
    fun getNews(): Call<NewsResponse>

    @GET("api/public/products")
    fun getProducts(): Call<ProductResponse>

    @POST("api/redeem/product/{id}")
    fun redeemProduct(@Path("id") productId: Int): Call<Void> // หรือเปลี่ยน Void เป็น Response Model ของคุณ

    @GET("user") // หรือ endpoint ที่คุณตั้งไว้ใน Laravel (เช่น profile)
    fun getUserProfile(@Header("Authorization") token: String): Call<UserResponse>
    @GET("user/points/wallet")
    fun getWallet(@Header("Authorization") token: String): Call<WalletResponse>


    @GET("user/points/history")
    fun getPointHistory(@Header("Authorization") token: String): Call<PointHistoryResponse>
}