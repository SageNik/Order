package com.example.order.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class RabbitMqConfig {

    public static final String ACCOUNT_MESSAGE_QUEUE = "account-message-queue";
    public static final String ACCOUNT_REPLY_MESSAGE_QUEUE = "account-reply-message-queue";
    public static final String ACCOUNT_RETURN_PAYMENT_MESSAGE_QUEUE = "account-return-payment-message-queue";
    public static final String MANUFACTURE_MESSAGE_QUEUE = "manufacture-message-queue";
    public static final String MANUFACTURE_REPLY_MESSAGE_QUEUE = "manufacture-reply-message-queue";
    public static final String MANUFACTURE_CANCEL_MESSAGE_QUEUE = "manufacture-cancel-message-queue";
    public static final String DELIVERY_MESSAGE_QUEUE = "delivery-message-queue";
    public static final String DELIVERY_REPLY_MESSAGE_QUEUE = "delivery-reply-message-queue";
    public static final String CANCELLATION_MESSAGE_QUEUE = "cancellation-message-queue";

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper());
    }

    private ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.modulesToInstall(new JavaTimeModule());
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return builder.build();
    }

    @Bean
    public Queue accountQueue() {
        return QueueBuilder.durable(ACCOUNT_MESSAGE_QUEUE).build();
    }

    @Bean
    public Queue accountReplyQueue() {
        return QueueBuilder.durable(ACCOUNT_REPLY_MESSAGE_QUEUE).build();
    }

    @Bean
    public Queue manufactureQueue() {
        return QueueBuilder.durable(MANUFACTURE_MESSAGE_QUEUE).build();
    }

    @Bean
    public Queue manufactureReplyQueue() {
        return QueueBuilder.durable(MANUFACTURE_REPLY_MESSAGE_QUEUE).build();
    }

    @Bean
    public Queue deliveryQueue() {
        return QueueBuilder.durable(DELIVERY_MESSAGE_QUEUE).build();
    }

    @Bean
    public Queue deliveryReplyQueue() {
        return QueueBuilder.durable(DELIVERY_REPLY_MESSAGE_QUEUE).build();
    }

    @Bean
    public Queue cancellationQueue() {
        return QueueBuilder.durable(CANCELLATION_MESSAGE_QUEUE).build();
    }
}
