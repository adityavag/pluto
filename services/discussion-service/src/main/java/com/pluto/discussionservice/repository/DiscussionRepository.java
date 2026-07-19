package com.pluto.discussionservice.repository;

import com.pluto.discussionservice.entity.Discussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, UUID> {

    // Fetch all general discussions (not tied to any post)
    Page<Discussion> findByPostIdIsNull(Pageable pageable);

    // Fetch all discussions for a specific post
    Page<Discussion> findByPostId(UUID postId, Pageable pageable);
}
