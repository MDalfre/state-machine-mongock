package com.example.statemachinemongock.order.statemachine

enum class OrderState {
    PROCESSED,
    PAYMENT_PENDING,
    PAYMENT_APPROVED,
    PAYMENT_FAILED,
    DELIVERY_STARTED,
    FINISHED
}