package com.pluto.submissionservice.service.impl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pluto.submissionservice.dto.request.CreateSubmissionRequest;
import com.pluto.submissionservice.dto.response.SubmissionResponse;
import com.pluto.submissionservice.entity.Submission;
import com.pluto.submissionservice.repository.SubmissionRepository;
import com.pluto.submissionservice.service.SubmissionService;
import com.pluto.submissionservice.service.client.ProblemServiceClient;
import com.pluto.submissionservice.service.queue.MessageQueueService;
import com.pluto.submissionservice.service.storage.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProblemServiceClient problemServiceClient;
    private final StorageService storageService;
    private final MessageQueueService messageQueueService;

    @Override
    public SubmissionResponse createSubmission(CreateSubmissionRequest request, String userId) {
        if (request.getProblemId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Problem ID must be provided");
        }
        if (request.getExcalidrawJson() == null || request.getExcalidrawJson().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excalidraw JSON diagram must be provided");
        }
        if (request.getWriteup() == null || request.getWriteup().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Writeup text explanation must be provided");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be provided");
        }

        // 1. Fetch rubric & prompt (which validates problem existence)
        ProblemServiceClient.ProblemDto problem = problemServiceClient.getProblemById(request.getProblemId());

        // 2. Write metadata to database (PENDING)
        Submission submission = new Submission();
        submission.setProblemId(request.getProblemId());
        submission.setUserId(userId);
        submission.setWriteup(request.getWriteup());
        submission.setStatus("PENDING");
        
        submission = submissionRepository.save(submission);

        // 3. Upload Excalidraw JSON (S3 mock)
        String fileName = "submission-" + submission.getId();
        String jsonUrl = storageService.storeJson(fileName, request.getExcalidrawJson());
        submission.setExcalidrawJsonUrl(jsonUrl);
        
        submission = submissionRepository.save(submission);

        // 4. Queue for evaluation
        messageQueueService.queueEvaluation(
                submission.getId(),
                problem.getTitle(),
                problem.getDescription(),
                request.getExcalidrawJson(),
                request.getWriteup()
        );

        return mapToResponse(submission);
    }

    @Override
    public SubmissionResponse getSubmissionById(UUID id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));
        return mapToResponse(submission);
    }

    private SubmissionResponse mapToResponse(Submission submission) {
        return new SubmissionResponse(
                submission.getId(),
                submission.getProblemId(),
                submission.getUserId(),
                submission.getExcalidrawJsonUrl(),
                submission.getWriteup(),
                submission.getStatus(),
                submission.getFeedback(),
                submission.getParsedDiagram(),
                submission.getCreatedAt(),
                submission.getUpdatedAt()
        );
    }
}
