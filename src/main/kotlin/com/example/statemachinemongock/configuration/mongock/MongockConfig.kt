package com.example.statemachinemongock.configuration.mongock

import com.example.statemachinemongock.order.OrderService
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver
import com.github.cloudyrock.spring.v5.MongockSpring5
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MongockConfig {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    val changelogPackage = "com.example.statemachinemongock.configuration.mongock.changelog"
    private val allowCollectionCreation = true
    private val changelogCollectionName = "statemachineChangelog"

    @Bean
    fun mongockBuilder(
        mongoTemplate: MongoTemplate,
        applicationContext: ApplicationContext,
        queueMessagingTemplate: QueueMessagingTemplate,
        orderService: OrderService
    ): MongockSpring5.MongockApplicationRunner {

        logger.info("Starting Mongock ...")

        val driver = SpringDataMongoV3Driver
            .withDefaultLock(mongoTemplate)

        driver.changeLogRepositoryName = changelogCollectionName
        driver.isIndexCreation = allowCollectionCreation

        return MongockSpring5.builder()
            .setDriver(driver)
            .addChangeLogsScanPackage(changelogPackage)
            .setSpringContext(applicationContext)
            .addDependency(orderService)
            .buildApplicationRunner()
    }
}