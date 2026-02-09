package com.purit.apptest.models

import com.google.gson.annotations.SerializedName

/**
 * คลาสสำหรับรับข้อมูล Response หลักจาก API /api/public/categories
 */
data class CategoryResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<CategoryItem>
)

/**
 * คลาสสำหรับข้อมูลหมวดหมู่แต่ละรายการ
 */
data class CategoryItem(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val icon: String? // รับ URL ไฟล์ .svg มา
)