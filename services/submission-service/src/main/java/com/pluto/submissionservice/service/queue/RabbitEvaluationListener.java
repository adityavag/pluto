package com.pluto.submissionservice.service.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pluto.submissionservice.config.RabbitMQConfig;
import com.pluto.submissionservice.dto.SubmissionEvaluationMessage;
import com.pluto.submissionservice.entity.Submission;
import com.pluto.submissionservice.repository.SubmissionRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitEvaluationListener {

    private final SubmissionRepository submissionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${pluto.evaluationservice.url}")
    private String evaluationServiceUrl;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeEvaluationMessage(SubmissionEvaluationMessage message) {
        log.info("Received evaluation request from RabbitMQ for submission: {}", message.getSubmissionId());

        try {
            // Build the evaluation request payload
            EvaluationRequest request = new EvaluationRequest(
                    message.getProblemTitle(),
                    message.getProblemDescription(),
                    message.getExcalidrawJson(),
                    message.getWriteup(),
                    null // rubric is optional
            );

            String url = evaluationServiceUrl + "/evaluate";
            log.info("Forwarding to evaluation-service at: {}", url);

            EvaluationResponse response = restTemplate.postForObject(url, request, EvaluationResponse.class);

            if (response != null) {
                // Update submission status to COMPLETED
                Submission submission = submissionRepository.findById(message.getSubmissionId()).orElse(null);
                if (submission != null) {
                    submission.setStatus("COMPLETED");
                    submission.setFeedback(response.getFeedback());
                    submission.setParsedDiagram(response.getParsedDiagram());
                    submissionRepository.save(submission);
                    log.info("Successfully processed submission {} and updated status to COMPLETED.", message.getSubmissionId());
                }
            } else {
                markAsFailed(message.getSubmissionId(), "Received empty response from evaluation service.");
            }
        } catch (Exception e) {
            log.error("Failed to evaluate submission {} in consumer: {}", message.getSubmissionId(), e.getMessage(), e);
            markAsFailed(message.getSubmissionId(), "Evaluation failed: " + e.getMessage());
        }
    }

    private void markAsFailed(java.util.UUID submissionId, String reason) {
        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission != null) {
            submission.setStatus("FAILED");
            submission.setFeedback(reason);
            submissionRepository.save(submission);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EvaluationRequest {
        @JsonProperty("problem_title")
        private String problemTitle;
        
        @JsonProperty("problem_description")
        private String problemDescription;
        
        @JsonProperty("excalidraw_json")
        private java.util.Map<String, Object> excalidrawJson;
        
        private String writeup;
        private String rubric;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EvaluationResponse {
        @JsonProperty("parsed_diagram")
        private String parsedDiagram;
        
        private String feedback;
    }
}
