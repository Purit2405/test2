package com.purit.apptest.models

// คลาสนี้รองรับโครงสร้างชั้นนอกสุดของ JSON
data class ProductResponse(
    val success: Boolean,
    val data: List<ProductItem> // ลิงก์ไปยังรายการสินค้า
)