package com.example.statemachinemongock.order.statemachine.configuration

import com.example.statemachinemongock.order.OrderNotFoundException
import com.example.statemachinemongock.order.OrderRepository
import com.example.statemachinemongock.order.OrderService.Companion.ORDER_NR_HEADER
import com.example.statemachinemongock.order.OrderService.Companion.STATE_ERROR
import com.example.statemachinemongock.order.statemachine.OrderEvent
import com.example.statemachinemongock.order.statemachine.OrderState
import com.mongodb.MongoException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.Message
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.state.State
import org.springframework.statemachine.support.StateMachineInterceptorAdapter
import org.springframework.statemachine.transition.Transition
import org.springframework.stereotype.Component

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
        try {
            val orderId = message?.let {
                it.headers[ORDER_NR_HEADER]?.toString()
            } ?: throw OrderNotFoundException("Required orderId header not found")

            logger.info(
                """[StateMachine] preStateChange
                   [OrderId] $orderId
                   changing state from: ${transition.source.id} to: ${transition.target.id}
                """.trimIndent()
            )

            val order = orderRepository.findByIdOrNull(orderId) ?: throw OrderNotFoundException(orderId)
            orderRepository.save(order.copy(state = state.id))
        } catch (ex: OrderNotFoundException) {
            stateMachine.setStateMachineError(ex)
            throw ex
        } catch (ex: MongoException) {
            stateMachine.setStateMachineError(ex)
            throw ex
        }
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
        stateMachine: StateMachine<OrderState, OrderEvent>,
        exception: Exception
    ): Exception {
        stateMachine.extendedState.variables[STATE_ERROR] = exception
        return exception
    }
}
