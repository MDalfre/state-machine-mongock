package com.example.statemachinemongock.order.statemachine.configuration

import com.example.statemachinemongock.order.statemachine.OrderEvent
import com.example.statemachinemongock.order.statemachine.OrderState
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer

@EnableStateMachineFactory
@Configuration
class Configuration(

) : StateMachineConfigurerAdapter<OrderState, OrderEvent>() {

    override fun configure(states: StateMachineStateConfigurer<OrderState, OrderEvent>) {
        super.configure(states)
    }
}