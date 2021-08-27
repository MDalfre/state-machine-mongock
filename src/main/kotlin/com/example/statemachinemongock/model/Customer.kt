package com.example.statemachinemongock.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Customer(
    @Id
    val id: String,
    val name: String,
    val document: String,
    val address: List<Address>,
    val phone: List<Phone>
)

data class Address (
    val street: String,
    val zipCode: String
        )

data class Phone (
    val number: Long
        )