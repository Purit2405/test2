package com.purit.apptest.models

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("data") val data: List<NewsItem>
)

data class NewsItem(
    val id: Int,
    val title: String,
    val content: String,
    val image: String?,
    @SerializedName("publish_date") val publishDate: String?
)