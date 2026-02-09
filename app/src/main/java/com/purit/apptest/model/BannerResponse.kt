package com.purit.apptest.models

data class BannerResponse(
    val success: Boolean,
    val data: List<BannerItem>
)

data class BannerItem(
    val id: Int,
    val image: String?, // URL รูปภาพจาก Laravel
    val link: String?
)