package com.pluto.submissionservice.service;

import java.util.UUID;

import com.pluto.submissionservice.dto.request.CreateSubmissionRequest;
import com.pluto.submissionservice.dto.response.SubmissionResponse;

public interface SubmissionService {
    SubmissionResponse createSubmission(CreateSubmissionRequest request, String userId);
    SubmissionResponse getSubmissionById(UUID id);
    java.util.List<SubmissionResponse> getSubmissionsByUserId(String userId);
}
