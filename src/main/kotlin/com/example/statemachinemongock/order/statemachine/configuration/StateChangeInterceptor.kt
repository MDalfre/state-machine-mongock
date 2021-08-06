package com.example.statemachinemongock.order.statemachine.configuration

import com.example.statemachinemongock.order.OrderRepository
import com.example.statemachinemongock.order.OrderNotFoundException
import com.example.statemachinemongock.order.statemachine.OrderEvent
import com.example.statemachinemongock.order.statemachine.OrderState
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.Message
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.state.State
import org.springframework.statemachine.support.StateMachineInterceptorAdapter
import org.springframework.statemachine.transition.Transition
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class StateChangeInterceptor(
    private val orderRepository: OrderRepository
) : StateMachineInterceptorAdapter<OrderState, OrderEvent>() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun preStateChange(
        state: State<OrderState, OrderEvent>,
        message: Message<OrderEvent>?,
        transition: Transition<OrderState, OrderEvent>,
        stateMachine: StateMachine<OrderState, OrderEvent>
    ) {
        val orderId = message?.let {
            it.headers["ORDER_ID"].toString()
        } ?: throw IllegalArgumentException("Required orderId header not found")

        logger.info(
            """
                [StateMachine] preStateChange
                [OrderId] $orderId
                changing state from: ${transition.source.id} to: ${transition.target.id}
            """.trimIndent()
        )

        val order = orderRepository.findByIdOrNull(orderId) ?: throw OrderNotFoundException(orderId)
        orderRepository.save(order.copy(state = state.id))
    }

    override fun postStateChange(
        state: State<OrderState, OrderEvent>,
        message: Message<OrderEvent>?,
        transition: Transition<OrderState, OrderEvent>,
        stateMachine: StateMachine<OrderState, OrderEvent>
    ) {
        super.postStateChange(state, message, transition, stateMachine)
    }

    override fun stateMachineError(
        stateMachine: StateMachine<OrderState, OrderEvent>?,
        exception: Exception
    ): Exception {
        logger.error("error found ${exception.message}")
        return exception
    }
}
