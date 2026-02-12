package com.purit.apptest.models

// ชั้นนอกสุดของ API
data class PointHistoryResponse(
    val success: Boolean,
    val data: PointPaginationData
)

// ชั้นที่เก็บข้อมูล Pagination และรายการข้อมูล
data class PointPaginationData(
    val current_page: Int,
    val data: List<PointTransactionItem>, // รายการประวัติจะอยู่ในนี้
    val total: Int,
    val last_page: Int,
    val per_page: Int,
    val next_page_url: String?,
    val prev_page_url: String?
)

// ข้อมูลแต่ละรายการ
data class PointTransactionItem(
    val id: Int,
    val type: String,        // reward | redeem
    val points: Int,         // มาเป็นตัวเลข เช่น 5000 หรือ -100
    val description: String?,
    val source_type: String?,
    val source_id: Int?,
    val source_name: String,
    val created_at: String
)