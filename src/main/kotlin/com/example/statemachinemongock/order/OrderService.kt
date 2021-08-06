package com.example.statemachinemongock.order

import com.example.statemachinemongock.order.statemachine.OrderEvent
import com.example.statemachinemongock.order.statemachine.OrderState
import com.example.statemachinemongock.order.statemachine.configuration.StateChangeInterceptor
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createOrder(order: Order) {
        logger.info("creating order")
        val orderStateMachine = buildOrderStateMachine(order.id, order.state)
    }

    private fun buildOrderStateMachine(
        orderId: String,
        orderState: OrderState
    ): OrderStateMachine =
        stateMachineFactory.getStateMachine(orderId).apply {
            stop()
            stateMachineAccessor.doWithAllRegions {
                it.addStateMachineInterceptor(orderStateMachineInterceptor)
                it.resetStateMachine(
                    DefaultStateMachineContext<OrderState, OrderEvent>(
                        orderState,
                        null, null, null
                    )
                )
            }
            start()
        }
}
