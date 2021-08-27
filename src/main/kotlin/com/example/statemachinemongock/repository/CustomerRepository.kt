package com.example.statemachinemongock.repository

import com.example.statemachinemongock.model.Customer
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : MongoRepository<Customer, String>