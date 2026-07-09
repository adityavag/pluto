package com.pluto.submissionservice.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionEvaluationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID submissionId;
    private String problemTitle;
    private String problemDescription;
    private Map<String, Object> excalidrawJson;
    private String writeup;
}
