package com.example.statemachinemongock.configuration.mongock.changelog

import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ChangeLog
class DatabaseChangelog {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @ChangeSet(order = "001", id = "test", author = "Marcio Henrique Dalfre")
    fun changelog1(mongockTemplate: MongockTemplate) {
        logger.info("Just testing changelog")
    }
}