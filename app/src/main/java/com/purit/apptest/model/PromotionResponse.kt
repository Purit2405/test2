package com.purit.apptest.models
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PromotionResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("points_value") val points_value: Int,
    @SerializedName("type") val type: String, // "reward" หรือ "redeem"
    @SerializedName("is_active") val is_active: Int
) : Serializable