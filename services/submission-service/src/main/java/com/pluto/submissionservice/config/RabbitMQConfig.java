package com.pluto.submissionservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "submission-evaluation-queue";
    public static final String EXCHANGE_NAME = "submission-exchange";
    public static final String ROUTING_KEY = "submission.eval";

    @Bean
    public Queue evaluationQueue() {
        return new Queue(QUEUE_NAME, true); // durable
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue evaluationQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(evaluationQueue).to(topicExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
