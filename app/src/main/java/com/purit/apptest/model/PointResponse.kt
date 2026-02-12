package com.purit.apptest.models

data class PointResponse(
    val success: Boolean,
    val data: WalletData
)

data class WalletData(
    val balance: Int
)
