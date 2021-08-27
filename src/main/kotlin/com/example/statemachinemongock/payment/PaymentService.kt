package com.example.statemachinemongock.payment

import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentProducer: PaymentProducer
) {

    fun paymentFeatureToggle(): Boolean = true

    fun producePaymentEvent(orderNr: String) {
        paymentProducer.sendToPaymentQueue(orderNr)
    }
}