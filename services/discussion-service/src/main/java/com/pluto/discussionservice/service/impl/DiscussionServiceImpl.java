package com.pluto.discussionservice.service.impl;

import com.pluto.discussionservice.dto.request.CreateDiscussionRequest;
import com.pluto.discussionservice.dto.response.DiscussionResponse;
import com.pluto.discussionservice.dto.response.PaginatedResponse;
import com.pluto.discussionservice.entity.Discussion;
import com.pluto.discussionservice.repository.DiscussionRepository;
import com.pluto.discussionservice.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscussionServiceImpl implements DiscussionService {

    private final DiscussionRepository discussionRepository;

    @Override
    public PaginatedResponse<DiscussionResponse> getAllDiscussions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toPagedResponse(discussionRepository.findByPostIdIsNull(pageable));
    }

    @Override
    public PaginatedResponse<DiscussionResponse> getDiscussionsByPostId(UUID postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toPagedResponse(discussionRepository.findByPostId(postId, pageable));
    }

    @Override
    public DiscussionResponse createDiscussion(String authorUsername, CreateDiscussionRequest request) {
        Discussion discussion = buildDiscussion(null, authorUsername, request);
        return mapToResponse(discussionRepository.save(discussion));
    }

    @Override
    public DiscussionResponse createDiscussionForPost(UUID postId, String authorUsername, CreateDiscussionRequest request) {
        Discussion discussion = buildDiscussion(postId, authorUsername, request);
        return mapToResponse(discussionRepository.save(discussion));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Discussion buildDiscussion(UUID postId, String authorUsername, CreateDiscussionRequest request) {
        Discussion discussion = new Discussion();
        discussion.setPostId(postId);
        discussion.setAuthorUsername(authorUsername);
        discussion.setTitle(request.getTitle());
        discussion.setContent(request.getContent());
        return discussion;
    }

    private PaginatedResponse<DiscussionResponse> toPagedResponse(Page<Discussion> page) {
        List<DiscussionResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private DiscussionResponse mapToResponse(Discussion discussion) {
        return new DiscussionResponse(
                discussion.getId(),
                discussion.getPostId(),
                discussion.getAuthorUsername(),
                discussion.getTitle(),
                discussion.getContent(),
                discussion.getCreatedAt(),
                discussion.getUpdatedAt()
        );
    }
}
