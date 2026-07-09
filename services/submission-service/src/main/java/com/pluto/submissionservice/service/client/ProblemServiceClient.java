package com.pluto.submissionservice.service.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import lombok.Data;

@Service
public class ProblemServiceClient {

    private final RestTemplate restTemplate;
    private final String problemServiceUrl;

    public ProblemServiceClient(@Value("${pluto.problemservice.url}") String problemServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.problemServiceUrl = problemServiceUrl;
    }

    public ProblemDto getProblemById(UUID id) {
        String url = problemServiceUrl + "/problems/" + id;
        try {
            return restTemplate.getForObject(url, ProblemDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found in problem-service");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error calling problem-service: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot connect to problem-service: " + e.getMessage());
        }
    }

    @Data
    public static class ProblemDto {
        private UUID id;
        private String slug;
        private String title;
        private String description;
        private String difficulty;
    }
}
