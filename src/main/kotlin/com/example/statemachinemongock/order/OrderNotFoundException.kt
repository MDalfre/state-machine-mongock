package com.example.statemachinemongock.order

class OrderNotFoundException(orderId: String) : RuntimeException("Order with $orderId not found")