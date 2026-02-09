package com.purit.apptest.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductItem(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val image: String?,

    // ตรวจสอบชื่อตรงนี้ ต้องสะกดแบบนี้เป๊ะๆ
    @SerializedName("redeemable")
    val redeemable: Boolean,

    @SerializedName("points_required")
    val points_required: Int,

    val category: CategoryData?
) : Parcelable

@Parcelize
data class CategoryData(
    val id: Int,
    val name: String
) : Parcelable