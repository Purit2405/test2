package com.purit.apptest.api

import com.purit.apptest.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // --- 1. Authentication (ระบบสมาชิก) ---
    @POST("api/auth/login")
    fun login(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<LoginResponse>

    @POST("api/auth/forgot-password")
    fun forgotPassword(@Body body: Map<String, String>): Call<BaseResponse>


    // --- 2. Public Data (ไม่ต้องใช้ Token) ---
    @GET("api/public/banners")
    fun getBanners(): Call<BannerResponse>

    @GET("api/public/categories")
    fun getCategories(): Call<CategoryResponse>

    @GET("api/public/news")
    fun getNews(): Call<NewsResponse>

    @GET("api/public/products")
    fun getProducts(): Call<ProductResponse>

    // ดึงโปรโมชั่น (ปรับ Path ให้ตรงกับ Laravel: api/public/promotions)
    @GET("api/public/promotions")
    fun getPromotions(): Call<List<PromotionResponse>>


    // --- 3. User & Points (ต้องส่ง Bearer Token) ---
    @GET("api/user/me")
    fun getUserProfile(@Header("Authorization") token: String): Call<UserProfileResponse>

    @GET("api/user/points/wallet")
    fun getWalletBalance(@Header("Authorization") token: String): Call<PointResponse>

    @GET("api/user/points/history")
    fun getPointHistory(@Header("Authorization") token: String): Call<PointHistoryResponse>


    // --- 4. Redemption (ระบบแลกแต้ม - ต้องส่ง Bearer Token) ---

    // แลกสินค้า
    @POST("api/redeem/product/{id}")
    fun redeemProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: Int
    ): Call<Void>

    // แลกโปรโมชั่น (ใช้ RedeemResponse ที่แยกไฟล์ไว้)
    @POST("api/redeem/promotion/{id}")
    fun redeemPromotion(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<RedeemResponse>

    // ออกจากระบบฝั่ง Server
    @POST("api/user/logout")
    fun logoutFromServer(@Header("Authorization") token: String): Call<RedeemResponse>
}