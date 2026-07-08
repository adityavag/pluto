package com.pluto.problemservice.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pluto.problemservice.dto.request.CreateProblemRequest;
import com.pluto.problemservice.dto.request.UpdateProblemRequest;
import com.pluto.problemservice.dto.response.ProblemResponse;
import com.pluto.problemservice.service.ProblemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/problems")
@RequiredArgsConstructor
public class AdminProblemController {

    private final ProblemService problemService;

    // createProblem
    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(@RequestBody CreateProblemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(problemService.createProblem(request));
    }

    // updateProblem
    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponse> updateProblem(
            @PathVariable UUID id, @RequestBody UpdateProblemRequest request) {
        return ResponseEntity.ok(problemService.updateProblem(id, request));
    }

    // deleteProblem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable UUID id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
