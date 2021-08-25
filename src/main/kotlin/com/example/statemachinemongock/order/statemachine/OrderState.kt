package com.example.statemachinemongock.order.statemachine

enum class OrderState {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_APPROVED,
    PAYMENT_FAILED,
    DELIVERY,
    DELIVERY_FAILED,
    CANCELLED,
    FINISHED
}