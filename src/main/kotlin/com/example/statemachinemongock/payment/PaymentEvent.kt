package com.example.statemachinemongock.payment

import java.time.LocalDateTime

data class PaymentEvent(
    val orderNr: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
