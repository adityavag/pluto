package com.pluto.problemservice.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pluto.problemservice.dto.request.CreateProblemRequest;
import com.pluto.problemservice.dto.request.ProblemFilterRequest;
import com.pluto.problemservice.dto.request.UpdateProblemRequest;
import com.pluto.problemservice.dto.response.PaginatedResponse;
import com.pluto.problemservice.dto.response.ProblemResponse;
import com.pluto.problemservice.dto.response.ProblemSummaryResponse;
import com.pluto.problemservice.repository.ProblemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public PaginatedResponse<ProblemSummaryResponse> getAllProblems(ProblemFilterRequest filterRequest) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ProblemResponse getProblemById(UUID id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ProblemResponse getProblemBySlug(String slug) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ProblemResponse createProblem(CreateProblemRequest request) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ProblemResponse updateProblem(UUID id, UpdateProblemRequest request) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void deleteProblem(UUID id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
