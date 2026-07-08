package com.pluto.problemservice.service;

import java.util.UUID;

import com.pluto.problemservice.dto.request.CreateProblemRequest;
import com.pluto.problemservice.dto.request.ProblemFilterRequest;
import com.pluto.problemservice.dto.request.UpdateProblemRequest;
import com.pluto.problemservice.dto.response.PaginatedResponse;
import com.pluto.problemservice.dto.response.ProblemResponse;
import com.pluto.problemservice.dto.response.ProblemSummaryResponse;

public interface ProblemService {

    PaginatedResponse<ProblemSummaryResponse> getAllProblems(ProblemFilterRequest filterRequest);

    ProblemResponse getProblemById(UUID id);

    ProblemResponse getProblemBySlug(String slug);

    ProblemResponse createProblem(CreateProblemRequest request);

    ProblemResponse updateProblem(UUID id, UpdateProblemRequest request);

    void deleteProblem(UUID id);
}
