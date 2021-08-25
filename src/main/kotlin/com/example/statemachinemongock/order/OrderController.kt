package com.example.statemachinemongock.order

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(@RequestBody order: OrderRequest) {
        orderService.createOrder(order)
    }

    @GetMapping("/payment/{orderNr}")
    fun receivePayment(@PathVariable("orderNr") orderNr: String) {
        orderService.receivePayment(orderNr)
    }

    @GetMapping("/payment/error/{orderNr}")
    fun receivePaymentError(@PathVariable("orderNr") orderNr: String) {
        orderService.receivePaymentError(orderNr)
    }

}