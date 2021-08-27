package com.example.statemachinemongock.configuration.mongock.changelog

import com.example.statemachinemongock.configuration.mongock.changelog.seeder.Seeder
import com.example.statemachinemongock.model.Address
import com.example.statemachinemongock.model.Customer
import com.example.statemachinemongock.order.OrderRequest
import com.example.statemachinemongock.order.OrderService
import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

@ChangeLog
class DatabaseChangelog {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @ChangeSet(order = "001", id = "[Local] Client Seeds", author = "Marcio Henrique Dalfre")
    fun changelog1(mongockTemplate: MongockTemplate) {
        val customerList = Seeder().generateCustomer(1000)
        mongockTemplate.insertAll(customerList)
        logger.info("Inserted ${customerList.size} customers to db.")
    }

    @ChangeSet(order = "002", id = "[Local] Client alterations", author = "Marcio Henrique Dalfre")
    fun changelog2(mongockTemplate: MongockTemplate) {
        val customerName = "123"
        val query = Query.query(
            Criteria.where("name").`is`(customerName)
        )
        mongockTemplate.findOne(query, Customer::class.java)?.let { customer ->
            val newAddress = Address(street = "Jose da Cunha, 249", zipCode = "13488-135")
            val customerUpdate = customer.copy(address = listOf(newAddress))

            mongockTemplate.save(customerUpdate)
        } ?: run { logger.error("Customer not found !") }
    }

    @ChangeSet(order = "003", id = "[Local] Generate orders", author = "Marcio Henrique Dalfre")
    fun changelog3(mongockTemplate: MongockTemplate, orderService: OrderService) {
        val productList = listOf("Ventilador", "Chinelo")

        mongockTemplate.findAll(Customer::class.java).let { customerList ->
            customerList.forEach { customer ->
                val order = OrderRequest(
                    product = productList,
                    address = customer.address.last().street
                )
                orderService.createOrder(order)
            }
        }
    }

    @ChangeSet(order = "004", id = "[Local] ", author = "Marcio Henrique Dalfre")
    fun changelog4() {

    }
}