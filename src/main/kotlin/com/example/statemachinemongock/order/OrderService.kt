package com.example.statemachinemongock.order

import com.example.statemachinemongock.order.statemachine.OrderEvent
import com.example.statemachinemongock.order.statemachine.OrderState
import com.example.statemachinemongock.order.statemachine.configuration.StateChangeInterceptor
import com.example.statemachinemongock.order.statemachine.sendEventOrThrow
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.support.DefaultStateMachineContext
import org.springframework.stereotype.Service

typealias OrderStateMachine = StateMachine<OrderState, OrderEvent>

@Service
class OrderService(
    private val stateMachineFactory: StateMachineFactory<OrderState, OrderEvent>,
    private val orderStateMachineInterceptor: StateChangeInterceptor,
    private val orderRepository: OrderRepository
) {
    companion object {
        const val ORDER_NR_HEADER = "ORDER_NR"
        const val STATE_ERROR = "STATE_ERROR"
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createOrder(orderRequest: OrderRequest): OrderResponse {
        logger.info("Creating order for $orderRequest")
        val order = orderRequest.buildOrder()
        orderRepository.save(order)

        val stateMachine = buildOrderStateMachine(order.orderNr, order.state)
        stateMachine.sendEventOrThrow(OrderEvent.CREATE_ORDER, order.orderNr)

        logger.info("Finished create order $order")
        return order.buildOrderResponse()
    }

    fun fetchOrder(orderNr: String): OrderResponse {
        logger.info("Getting order by id $orderNr")

        val order = orderRepository.findByIdOrNull(orderNr)
            ?: throw OrderNotFoundException(orderNr)

        return order.buildOrderResponse()
    }

    fun fetchAllOrders(): List<OrderResponse> {
        val orders = orderRepository.findAll()
        return orders.map { it.buildOrderResponse() }
    }

    fun sendEventToOrderStateMachine(orderNr: String, orderEvent: OrderEvent) {
        logger.info("Sending event ${orderEvent.name} for order $orderNr")
        val order = orderRepository.findByIdOrNull(orderNr)
            ?: throw OrderNotFoundException(orderNr)

        val orderStateMachine = buildOrderStateMachine(order.orderNr, order.state)
        orderStateMachine.sendEventOrThrow(orderEvent, orderNr)
    }

    private fun buildOrderStateMachine(
        orderId: String,
        orderState: OrderState
    ): OrderStateMachine =
        stateMachineFactory.getStateMachine(orderId).apply {
            stop()
            stateMachineAccessor.doWithAllRegions {
                it.addStateMachineInterceptor(orderStateMachineInterceptor)
                it.resetStateMachine(DefaultStateMachineContext(orderState, null, null, null))
            }
            start()
        }
}
