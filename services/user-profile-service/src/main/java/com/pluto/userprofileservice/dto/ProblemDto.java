package com.pluto.userprofileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDto {
    private UUID id;
    private String slug;
    private String title;
    private String difficulty;
}
