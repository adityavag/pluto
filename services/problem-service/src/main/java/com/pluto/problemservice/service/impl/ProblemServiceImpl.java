package com.pluto.problemservice.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pluto.problemservice.dto.request.CreateProblemRequest;
import com.pluto.problemservice.dto.request.ProblemFilterRequest;
import com.pluto.problemservice.dto.request.UpdateProblemRequest;
import com.pluto.problemservice.dto.response.PaginatedResponse;
import com.pluto.problemservice.dto.response.ProblemResponse;
import com.pluto.problemservice.dto.response.ProblemSummaryResponse;
import com.pluto.problemservice.entity.Problem;
import com.pluto.problemservice.repository.ProblemRepository;
import com.pluto.problemservice.service.ProblemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "problem_lists", key = "#filterRequest")
    public PaginatedResponse<ProblemSummaryResponse> getAllProblems(ProblemFilterRequest filterRequest) {
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());
        Page<Problem> problemPage;

        if (filterRequest.getDifficulty() != null && !filterRequest.getDifficulty().trim().isEmpty()) {
            problemPage = problemRepository.findByDifficulty(filterRequest.getDifficulty(), pageable);
        } else {
            problemPage = problemRepository.findAll(pageable);
        }

        List<ProblemSummaryResponse> content = problemPage.getContent().stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                problemPage.getNumber(),
                problemPage.getSize(),
                problemPage.getTotalElements(),
                problemPage.getTotalPages()
        );
    }

    @Override
    @Cacheable(value = "problems", key = "#id", sync = true)
    public ProblemResponse getProblemById(UUID id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));
        return mapToResponse(problem);
    }

    @Override
    @Cacheable(value = "problems_by_slug", key = "#slug", sync = true)
    public ProblemResponse getProblemBySlug(String slug) {
        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));
        return mapToResponse(problem);
    }

    @Override
    @CacheEvict(value = "problem_lists", allEntries = true)
    public ProblemResponse createProblem(CreateProblemRequest request) {
        if (request.getSlug() != null && problemRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Problem with slug '" + request.getSlug() + "' already exists");
        }

        Problem problem = new Problem();
        problem.setSlug(request.getSlug());
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());

        Problem savedProblem = problemRepository.save(problem);
        return mapToResponse(savedProblem);
    }

    @Override
    public ProblemResponse updateProblem(UUID id, UpdateProblemRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));

        String oldSlug = problem.getSlug();

        if (request.getSlug() != null && !request.getSlug().equals(oldSlug)) {
            if (problemRepository.findBySlug(request.getSlug()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Problem with slug '" + request.getSlug() + "' already exists");
            }
            problem.setSlug(request.getSlug());
            evictCacheKey("problems_by_slug", oldSlug);
        }

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());

        Problem updatedProblem = problemRepository.save(problem);
        ProblemResponse response = mapToResponse(updatedProblem);

        // Evict updated caches
        evictCacheKey("problems", id);
        evictCacheKey("problems_by_slug", response.getSlug());
        clearCache("problem_lists");

        return response;
    }

    @Override
    public void deleteProblem(UUID id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));

        problemRepository.delete(problem);

        // Evict deleted caches
        evictCacheKey("problems", id);
        evictCacheKey("problems_by_slug", problem.getSlug());
        clearCache("problem_lists");
    }

    private void evictCacheKey(String cacheName, Object key) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    private void clearCache(String cacheName) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    private ProblemResponse mapToResponse(Problem problem) {
        return new ProblemResponse(
                problem.getId(),
                problem.getSlug(),
                problem.getTitle(),
                problem.getDescription(),
                problem.getDifficulty()
        );
    }

    private ProblemSummaryResponse mapToSummaryResponse(Problem problem) {
        return new ProblemSummaryResponse(
                problem.getId(),
                problem.getSlug(),
                problem.getTitle(),
                problem.getDifficulty()
        );
    }
}
