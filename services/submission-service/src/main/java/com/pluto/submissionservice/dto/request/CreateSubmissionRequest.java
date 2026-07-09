package com.pluto.submissionservice.dto.request;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubmissionRequest {
    private UUID problemId;
    private Map<String, Object> excalidrawJson;
    private String writeup;
}
