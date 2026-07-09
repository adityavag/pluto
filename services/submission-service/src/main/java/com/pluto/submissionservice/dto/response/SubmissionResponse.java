package com.pluto.submissionservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private UUID id;
    private UUID problemId;
    private String excalidrawJsonUrl;
    private String writeup;
    private String status;
    private String feedback;
    private String parsedDiagram;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
