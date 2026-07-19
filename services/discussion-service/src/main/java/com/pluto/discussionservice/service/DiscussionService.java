package com.pluto.discussionservice.service;

import com.pluto.discussionservice.dto.request.CreateDiscussionRequest;
import com.pluto.discussionservice.dto.response.DiscussionResponse;
import com.pluto.discussionservice.dto.response.PaginatedResponse;

import java.util.UUID;

public interface DiscussionService {

    /**
     * Retrieve all general discussions (not tied to any specific post), paginated.
     */
    PaginatedResponse<DiscussionResponse> getAllDiscussions(int page, int size);

    /**
     * Retrieve all discussions for a specific post, paginated.
     */
    PaginatedResponse<DiscussionResponse> getDiscussionsByPostId(UUID postId, int page, int size);

    /**
     * Create a new general discussion. authorUsername is extracted from the JWT by the controller.
     */
    DiscussionResponse createDiscussion(String authorUsername, CreateDiscussionRequest request);

    /**
     * Create a new discussion linked to a specific post. authorUsername is extracted from the JWT by the controller.
     */
    DiscussionResponse createDiscussionForPost(UUID postId, String authorUsername, CreateDiscussionRequest request);
}
