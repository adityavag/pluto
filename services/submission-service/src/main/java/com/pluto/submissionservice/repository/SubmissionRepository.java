package com.pluto.submissionservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pluto.submissionservice.entity.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    java.util.List<Submission> findByUserIdOrderByCreatedAtDesc(String userId);
}
