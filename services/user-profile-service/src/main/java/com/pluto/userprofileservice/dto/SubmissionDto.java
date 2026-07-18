package com.pluto.userprofileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDto {
    private UUID id;
    private UUID problemId;
    private String userId;
    private String excalidrawJsonUrl;
    private String writeup;
    private String status;
    private String feedback;
    private String parsedDiagram;
    private LocalDateTime createdAt;
}
