package com.example.statemachinemongock.order

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(order: Order) {
        orderService.createOrder(order)
    }
}