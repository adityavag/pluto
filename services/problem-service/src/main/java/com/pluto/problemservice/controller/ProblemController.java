package com.pluto.problemservice.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pluto.problemservice.dto.request.ProblemFilterRequest;
import com.pluto.problemservice.dto.response.PaginatedResponse;
import com.pluto.problemservice.dto.response.ProblemResponse;
import com.pluto.problemservice.dto.response.ProblemSummaryResponse;
import com.pluto.problemservice.service.ProblemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // getAllProblems
    @GetMapping
    public ResponseEntity<PaginatedResponse<ProblemSummaryResponse>> getAllProblems(
            ProblemFilterRequest filterRequest) {
        return ResponseEntity.ok(problemService.getAllProblems(filterRequest));
    }

    // fetchProblemBySlug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProblemResponse> fetchProblemBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(problemService.getProblemBySlug(slug));
    }

    // fetchProblemById
    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> fetchProblemById(@PathVariable UUID id) {
        return ResponseEntity.ok(problemService.getProblemById(id));
    }
}
