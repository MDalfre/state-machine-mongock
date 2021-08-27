package com.example.statemachinemongock.payment

import com.example.statemachinemongock.order.OrderService
import com.example.statemachinemongock.order.statemachine.OrderEvent
import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.stereotype.Component

@Component
class OrderPaymentConsumer(private val orderService: OrderService) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener("order-payment-queue")
    fun consume(event: PaymentEvent) {
        Thread.sleep(1000)
        logger.info("Received payment event for order: ${event.orderNr}")
        orderService.sendEventToOrderStateMachine(
            orderNr = event.orderNr,
            orderEvent = OrderEvent.RECEIVE_PAYMENT
        )
    }
}