package com.purit.apptest.models
import com.google.gson.annotations.SerializedName

// --- สำหรับ User Profile ---
data class UserResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UserData
)
data class UserData(val id: Int, val name: String, val phone: String)

// --- สำหรับยอดแต้มคงเหลือ (Wallet) ---
data class WalletResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: WalletBalance
)
data class WalletBalance(val balance: Int)

// --- สำหรับประวัติ (History) รองรับ Laravel Paginate ---
data class PointHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val pagination: PointPagination
)
data class PointPagination(
    @SerializedName("data") val transactions: List<PointTransaction>
)
data class PointTransaction(
    val id: Int,
    val type: String,
    val points: Int,
    val description: String?,
    @SerializedName("source_name") val source_name: String?,
    @SerializedName("created_at") val created_at: String
)