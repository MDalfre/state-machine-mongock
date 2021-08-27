package com.example.statemachinemongock.order

import com.example.statemachinemongock.order.statemachine.OrderState
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class Order(
    @Id
    val orderNr: String = UUID.randomUUID().toString(),
    val product: List<String>,
    val address: String,
    val state: OrderState = OrderState.CREATED,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

fun Order.buildOrderResponse(): OrderResponse =
    OrderResponse(
        orderNr = orderNr,
        product = product,
        address = address,
        state = state,
        createdAt = createdAt
    )

data class OrderRequest(
    val product: List<String>,
    val address: String
)

fun OrderRequest.buildOrder(): Order =
    Order(product = this.product, address = this.address)


data class OrderResponse(
    val orderNr: String,
    val product: List<String>,
    val address: String,
    val state: OrderState,
    val createdAt: LocalDateTime
)
