package com.pluto.problemservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProblemRequest {
    private String slug;
    private String title;
    private String description;
    private String difficulty;
}
