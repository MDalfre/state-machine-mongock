package com.example.statemachinemongock.order

import com.example.statemachinemongock.order.statemachine.OrderEvent
import com.example.statemachinemongock.payment.PaymentEvent
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
    private val queueMessagingTemplate: QueueMessagingTemplate
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody order: OrderRequest): OrderResponse {
        return orderService.createOrder(order)
    }

    @GetMapping
    fun fetchAllOrders(): List<OrderResponse> {
        return orderService.fetchAllOrders()
    }

    @GetMapping("/{orderNr}")
    fun fetchOrder(@PathVariable orderNr: String): OrderResponse {
        return orderService.fetchOrder(orderNr)
    }

    @PatchMapping("/{orderNr}/payment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun receivePayment(@PathVariable("orderNr") orderNr: String) {
        orderService.sendEventToOrderStateMachine(
            orderNr = orderNr,
            orderEvent = OrderEvent.RECEIVE_PAYMENT
        )
    }

    @PatchMapping("/{orderNr}/payment/error")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun receivePaymentError(@PathVariable("orderNr") orderNr: String) {
        orderService.sendEventToOrderStateMachine(
            orderNr = orderNr,
            orderEvent = OrderEvent.NOTIFY_PAYMENT_ERROR
        )
    }

    @PatchMapping("/{orderNr}/delivery")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun startDelivery(@PathVariable("orderNr") orderNr: String) {
        orderService.sendEventToOrderStateMachine(
            orderNr = orderNr,
            orderEvent = OrderEvent.START_DELIVERY
        )
    }

    @PatchMapping("{orderNr}/delivery/error")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun receiveDeliveryError(@PathVariable("orderNr") orderNr: String) {
        orderService.sendEventToOrderStateMachine(
            orderNr = orderNr,
            orderEvent = OrderEvent.NOTIFY_DELIVERY_ERROR
        )
    }

    @PatchMapping("/{orderNr}/delivery/retry")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun retryDelivery(@PathVariable("orderNr") orderNr: String) {
        orderService.sendEventToOrderStateMachine(
            orderNr = orderNr,
            orderEvent = OrderEvent.RETRY_DELIVERY
        )
    }

    @PatchMapping("/{orderNr}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelOrder(@PathVariable("orderNr") orderNr: String) {
        orderService.sendEventToOrderStateMachine(
            orderNr = orderNr,
            orderEvent = OrderEvent.CANCEL_ORDER
        )
    }

    @PatchMapping("/{orderNr}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun completeOrder(@PathVariable("orderNr") orderNr: String) {
        orderService.sendEventToOrderStateMachine(
            orderNr = orderNr,
            orderEvent = OrderEvent.COMPLETE_ORDER
        )
    }

    @GetMapping("/test")
    fun getQ(): PaymentEvent? {
        return queueMessagingTemplate.receiveAndConvert(
            "order-payment-dlq",
            PaymentEvent::class.java
        )
    }
}
