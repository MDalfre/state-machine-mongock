package com.example.statemachinemongock.order.statemachine

enum class OrderEvent {
    CREATE_ORDER,
    RECEIVE_PAYMENT,
    NOTIFY_PAYMENT_ERROR,
    START_DELIVERY,
    NOTIFY_DELIVERY_ERROR,
    RETRY_DELIVERY,
    CANCEL_ORDER,
    COMPLETE_ORDER
}