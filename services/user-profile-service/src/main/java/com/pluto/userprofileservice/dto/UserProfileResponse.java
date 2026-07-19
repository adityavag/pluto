package com.pluto.userprofileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private UserInfoDto user;
    private SolvedStatsDto solvedStats;
    private Map<String, Integer> submissionCalendar;
    private StreakStatsDto streakStats;
    private List<RecentSubmissionDto> recentSubmissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfoDto {
        private String username;
        private Instant createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SolvedStatsDto {
        private int solvedCount;
        private int totalCount;
        
        private int easySolved;
        private int easyTotal;
        
        private int mediumSolved;
        private int mediumTotal;
        
        private int hardSolved;
        private int hardTotal;
        
        private int attemptingCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StreakStatsDto {
        private int totalActiveDays;
        private int maxStreak;
        private int currentStreak;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentSubmissionDto {
        private String id;
        private String problemId;
        private String problemTitle;
        private String problemSlug;
        private String difficulty;
        private String status;
        private String createdAt; // formatted datetime string
    }
}
