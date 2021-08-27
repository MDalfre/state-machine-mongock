package com.example.statemachinemongock.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.messaging.converter.MappingJackson2MessageConverter

@Configuration
class LocalMessagingConfiguration(
    @Value("\${cloud.aws.region.static}") val region: String,
    @Value("\${cloud.aws.endpoint}") val endpoint: String,
    @Value("\${cloud.aws.credentials.accessKey}") val accessKey: String,
    @Value("\${cloud.aws.credentials.secretKey}") val secretKey: String
) {

    @Bean
    fun notificationMessageTemplate(): NotificationMessagingTemplate {
        return NotificationMessagingTemplate(buildAmazonSNSAsync())
    }

    @Bean
    @Primary
    fun queueMessagingTemplate(): QueueMessagingTemplate {
        return QueueMessagingTemplate(buildAmazonSQSAsync())
    }

    @Bean
    fun simpleMessageListenerContainerFactory(): SimpleMessageListenerContainerFactory {
        val messageListenerContainerFactory = SimpleMessageListenerContainerFactory()
        messageListenerContainerFactory.setAmazonSqs(buildAmazonSQSAsync())
        return messageListenerContainerFactory
    }

    @Bean
    fun queueMessageHandlerFactory(objectMapper: ObjectMapper): QueueMessageHandlerFactory {
        val messageConverter = MappingJackson2MessageConverter()
        messageConverter.objectMapper = objectMapper

        val queueMessageHandlerFactory = QueueMessageHandlerFactory()
        queueMessageHandlerFactory.messageConverters = listOf(messageConverter)

        return queueMessageHandlerFactory
    }

    private fun buildAmazonSNSAsync(): AmazonSNSAsync {
        return AmazonSNSAsyncClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .build()
    }

    private fun buildAmazonSQSAsync(): AmazonSQSAsync {
        return AmazonSQSAsyncClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .build()
    }
}