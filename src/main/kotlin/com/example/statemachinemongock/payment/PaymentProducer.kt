package com.example.statemachinemongock.payment

import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.stereotype.Component

@Component
class PaymentProducer(
    private val queueMessagingTemplate: QueueMessagingTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendToPaymentQueue(orderNr: String) {
        val queue = "order-payment-queue"
        val event = PaymentEvent(orderNr)

        logger.info("Sending message to queue: $queue")
        queueMessagingTemplate.convertAndSend(queue, event)
    }
}