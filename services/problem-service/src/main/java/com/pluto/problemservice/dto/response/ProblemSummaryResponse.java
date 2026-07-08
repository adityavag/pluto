package com.pluto.problemservice.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSummaryResponse {
    private UUID id;
    private String slug;
    private String title;
    private String difficulty;
}
