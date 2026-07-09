package com.pluto.submissionservice.service.queue;

import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.pluto.submissionservice.config.RabbitMQConfig;
import com.pluto.submissionservice.dto.SubmissionEvaluationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMessageQueueService implements MessageQueueService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void queueEvaluation(UUID submissionId, String problemTitle, String problemDescription, Map<String, Object> excalidrawJson, String writeup) {
        log.info("Publishing evaluation request to RabbitMQ for submission: {}", submissionId);
        
        SubmissionEvaluationMessage message = new SubmissionEvaluationMessage(
                submissionId,
                problemTitle,
                problemDescription,
                excalidrawJson,
                writeup
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
        log.info("Successfully published submission {} evaluation request to RabbitMQ.", submissionId);
    }
}
