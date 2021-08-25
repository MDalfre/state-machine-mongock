package com.example.statemachinemongock.order.statemachine

import com.example.statemachinemongock.order.OrderService.Companion.ORDER_NR_HEADER
import com.example.statemachinemongock.order.OrderStateMachine
import org.springframework.messaging.support.MessageBuilder
import org.springframework.messaging.support.MessageHeaderAccessor

fun OrderStateMachine.sendEvent(
    event: OrderEvent,
    orderNr: String,
    headers: Map<String, String>? = null,
) {
    val msg = MessageBuilder.withPayload(event)
        .setHeaders(createMessageHeaders(orderNr, headers))
        .build()
    val eventAccepted = sendEvent(msg)

    if (!eventAccepted)
        throw InvalidStateTransitionException("Invalid transition for orderNr $orderNr with event $event")
}

private fun createMessageHeaders(
    orderNr: String,
    headers: Map<String, String>?
): MessageHeaderAccessor {
    val headersAccessor = MessageHeaderAccessor()

    headersAccessor.setHeader(ORDER_NR_HEADER, orderNr)
    headers?.forEach { (key, value) ->
        headersAccessor.setHeader(key, value)
    }

    return headersAccessor
}