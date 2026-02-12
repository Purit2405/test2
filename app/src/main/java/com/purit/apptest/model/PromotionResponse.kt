package com.purit.apptest.models

data class PromotionResponse(
    val id: Int,
    val title: String,
    val description: String?,
    val type: String,        // reward หรือ redeem
    val points_value: Int,   // ค่าแต้ม (เช่น 300 หรือ -100)
    val image: String?,      // path รูปภาพจาก laravel
    val is_active: Boolean,
    val max_total: Int?,
    val max_per_user: Int?
)