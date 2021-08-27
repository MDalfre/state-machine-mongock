package com.example.statemachinemongock.configuration.mongock.changelog.seeder

import com.example.statemachinemongock.configuration.mongock.changelog.seeder.NamePool.fistNamePool
import com.example.statemachinemongock.configuration.mongock.changelog.seeder.NamePool.lastNamePool
import com.example.statemachinemongock.model.Address
import com.example.statemachinemongock.model.Customer
import com.example.statemachinemongock.model.Phone
import org.apache.commons.lang3.RandomUtils.nextInt
import java.util.*

class Seeder {

    fun generateCustomer(count: Int = 5): List<Customer> {
        val customerList = mutableListOf<Customer>()
        (1..count).forEach { _ ->
            customerList.add(
                Customer(
                    id = UUID.randomUUID().toString(),
                    name = nameGenerator(),
                    document = numberGenerator(),
                    address = addressGenerator(),
                    phone = phoneGenerator()
                )
            )
        }
        return customerList
    }

    private fun phoneGenerator(): List<Phone> {
        val phoneList = mutableListOf<Phone>()
        val phoneCount = nextInt(2, 4)
        (1..phoneCount).forEach {
            phoneList.add(
                Phone(
                    number = numberGenerator(9).toLong()
                )
            )
        }
        return phoneList
    }

    private fun addressGenerator(): List<Address> {
        return listOf(
            Address(
                street = "Rua ${nameGenerator()}, ${numberGenerator(nextInt(2, 4))}",
                zipCode = "${numberGenerator(5)}-${numberGenerator(3)}"
            )
        )
    }

    private fun nameGenerator(): String {
        val firstName = fistNamePool.random()
        val lastName = lastNamePool.random()
        return "$firstName $lastName"
    }

    private fun numberGenerator(digits: Int = 11): String {
        var documentNumber = ""
        (0..digits).forEach { _ -> documentNumber += nextInt(0, 9).toString() }
        return documentNumber
    }
}