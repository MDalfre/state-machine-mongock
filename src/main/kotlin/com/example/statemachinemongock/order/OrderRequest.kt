package com.example.statemachinemongock.order

data class OrderRequest(
    val product: List<String>,
    val address: String
)


