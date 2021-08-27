package com.example.statemachinemongock.order.statemachine.configuration

import com.example.statemachinemongock.order.OrderService.Companion.ORDER_NR_HEADER
import com.example.statemachinemongock.order.statemachine.OrderEvent
import com.example.statemachinemongock.order.statemachine.OrderState
import com.example.statemachinemongock.order.statemachine.messageHeader
import com.example.statemachinemongock.payment.PaymentService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State

@EnableStateMachineFactory
@Configuration
class Configuration(
    private val paymentService: PaymentService
) : StateMachineConfigurerAdapter<OrderState, OrderEvent>() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun configure(stateConfigurer: StateMachineStateConfigurer<OrderState, OrderEvent>) {
        stateConfigurer.withStates()
            .initial(OrderState.CREATED)
            .states(OrderState.values().toSet())
            .end(OrderState.PAYMENT_FAILED)
            .end(OrderState.CANCELLED)
            .end(OrderState.FINISHED)
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<OrderState, OrderEvent>) {
        transitions.withExternal()
            .source(OrderState.CREATED).target(OrderState.PAYMENT_PENDING)
            .event(OrderEvent.CREATE_ORDER)
            .action { paymentService.producePaymentEvent(it.messageHeader(ORDER_NR_HEADER)) }
            .guard { paymentService.paymentFeatureToggle() }

            .and().withExternal()
            .source(OrderState.PAYMENT_PENDING).target(OrderState.PAYMENT_APPROVED)
            .event(OrderEvent.RECEIVE_PAYMENT)

            .and().withExternal()
            .source(OrderState.PAYMENT_PENDING).target(OrderState.PAYMENT_FAILED)
            .event(OrderEvent.NOTIFY_PAYMENT_ERROR)

            .and().withExternal()
            .source(OrderState.PAYMENT_PENDING).target(OrderState.CANCELLED)
            .event(OrderEvent.CANCEL_ORDER)

            .and().withExternal()
            .source(OrderState.PAYMENT_APPROVED).target(OrderState.DELIVERY)
            .event(OrderEvent.START_DELIVERY)

            .and().withExternal()
            .source(OrderState.DELIVERY).target(OrderState.DELIVERY_FAILED)
            .event(OrderEvent.NOTIFY_DELIVERY_ERROR)

            .and().withExternal()
            .source(OrderState.DELIVERY_FAILED).target(OrderState.DELIVERY)
            .event(OrderEvent.RETRY_DELIVERY)

            .and().withExternal()
            .source(OrderState.DELIVERY).target(OrderState.FINISHED)
            .event(OrderEvent.COMPLETE_ORDER)
    }

    override fun configure(config: StateMachineConfigurationConfigurer<OrderState, OrderEvent>) {
        val listener = object : StateMachineListenerAdapter<OrderState, OrderEvent>() {
            override fun stateChanged(
                from: State<OrderState, OrderEvent>,
                to: State<OrderState, OrderEvent>
            ) = logger.info("stateChanged(from: ${from.id}, to: ${to.id})")

            override fun eventNotAccepted(event: Message<OrderEvent>?) =
                logger.warn("eventNotAccepted payload: ${event?.payload} headers: ${event?.headers}")

            override fun stateMachineError(
                stateMachine: StateMachine<OrderState, OrderEvent>,
                exception: Exception
            ) =
                logger.error("stateMachineError state: ${stateMachine.state.id} exception: ${exception.message}")
        }

        config.withConfiguration().listener(listener)
    }
}
