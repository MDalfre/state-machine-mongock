package com.example.statemachinemongock.order

import com.example.statemachinemongock.order.statemachine.OrderEvent
import com.example.statemachinemongock.order.statemachine.OrderState
import com.example.statemachinemongock.order.statemachine.configuration.StateChangeInterceptor
import com.example.statemachinemongock.order.statemachine.sendEvent
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

    fun createOrder(orderRequest: OrderRequest) {
        logger.info("Creating order for $orderRequest")
        val order = orderRequest.buildOrder()
        orderRepository.save(order)

        val stateMachine = buildOrderStateMachine(order.orderNr, order.state)
        stateMachine.sendEvent(OrderEvent.CREATE_ORDER, order.orderNr)

        if (stateMachine.hasStateMachineError()) {
            throw stateMachine.extendedState.variables[STATE_ERROR] as Throwable
        }

        logger.info("Finished create order for $order")
    }

    fun receivePayment(orderNr: String) {
        val order = orderRepository.findByIdOrNull(orderNr)
            ?: throw OrderNotFoundException(orderNr)

        val orderStateMachine = buildOrderStateMachine(order.orderNr, order.state)
        orderStateMachine.sendEvent(OrderEvent.RECEIVE_PAYMENT, orderNr)
    }

    fun receivePaymentError(orderNr: String) {
        val order = orderRepository.findByIdOrNull(orderNr)
            ?: throw OrderNotFoundException(orderNr)

        val orderStateMachine = buildOrderStateMachine(order.orderNr, order.state)
        orderStateMachine.sendEvent(OrderEvent.NOTIFY_PAYMENT_ERROR, orderNr)
    }

    fun notifyDelivery(orderNr: String) {

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

private fun OrderRequest.buildOrder(): Order =
    Order(product = this.product, address = this.address)
