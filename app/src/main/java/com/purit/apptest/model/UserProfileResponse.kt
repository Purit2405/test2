package com.purit.apptest.models

data class UserProfileResponse(
    val success: Boolean,
    val data: UserProfile
)

data class UserProfile(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?
)
