package com.example.statemachinemongock.order

import com.example.statemachinemongock.order.statemachine.OrderState
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class Order(
    @Id
    val id: String,
    val orderNr: String = UUID.randomUUID().toString(),
    val state: OrderState = OrderState.PROCESSED,
    val createdAt: LocalDateTime = LocalDateTime.now()
)