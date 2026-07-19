package com.pluto.userprofileservice.client;

import com.pluto.userprofileservice.dto.UserDto;
import com.pluto.userprofileservice.dto.ProblemDto;
import com.pluto.userprofileservice.dto.SubmissionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@Service
public class ServiceClient {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;
    private final String problemServiceUrl;
    private final String submissionServiceUrl;

    public ServiceClient(
            @Value("${pluto.authservice.url}") String authServiceUrl,
            @Value("${pluto.problemservice.url}") String problemServiceUrl,
            @Value("${pluto.submissionservice.url}") String submissionServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.authServiceUrl = authServiceUrl;
        this.problemServiceUrl = problemServiceUrl;
        this.submissionServiceUrl = submissionServiceUrl;
    }

    public UserDto getUserByUsername(String username) {
        String url = authServiceUrl + "/account/users/" + username;
        try {
            return restTemplate.getForObject(url, UserDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in auth-service");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error calling auth-service: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot connect to auth-service: " + e.getMessage());
        }
    }

    public List<SubmissionDto> getSubmissionsByUserId(String userId) {
        String url = submissionServiceUrl + "/submissions/user/" + userId;
        try {
            ResponseEntity<List<SubmissionDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<SubmissionDto>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot connect to submission-service: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<ProblemDto> getAllProblems() {
        // Fetch up to 10,000 problems (practically all in our system)
        String url = problemServiceUrl + "/problems?size=10000";
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("content")) {
                return List.of();
            }
            
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) body.get("content");
            return contentList.stream().map(map -> {
                ProblemDto dto = new ProblemDto();
                dto.setId(UUID.fromString((String) map.get("id")));
                dto.setSlug((String) map.get("slug"));
                dto.setTitle((String) map.get("title"));
                dto.setDifficulty((String) map.get("difficulty"));
                return dto;
            }).collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot connect to problem-service: " + e.getMessage());
        }
    }
}
