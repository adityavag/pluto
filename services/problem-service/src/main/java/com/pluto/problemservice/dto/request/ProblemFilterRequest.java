package com.pluto.problemservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemFilterRequest {
    private String difficulty;
    private int page = 0;
    private int size = 10;
}
