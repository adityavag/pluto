package com.pluto.submissionservice.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pluto.submissionservice.dto.request.CreateSubmissionRequest;
import com.pluto.submissionservice.dto.response.SubmissionResponse;
import com.pluto.submissionservice.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> createSubmission(
            @RequestBody CreateSubmissionRequest request,
            @RequestHeader("X-User-Id") String userId) {
        SubmissionResponse response = submissionService.createSubmission(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> getSubmissionById(@PathVariable UUID id) {
        SubmissionResponse response = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<java.util.List<SubmissionResponse>> getSubmissionsByUserId(@PathVariable String userId) {
        java.util.List<SubmissionResponse> response = submissionService.getSubmissionsByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
