package com.pluto.userprofileservice.service;

import com.pluto.userprofileservice.client.ServiceClient;
import com.pluto.userprofileservice.dto.UserDto;
import com.pluto.userprofileservice.dto.ProblemDto;
import com.pluto.userprofileservice.dto.SubmissionDto;
import com.pluto.userprofileservice.dto.UserProfileResponse;
import com.pluto.userprofileservice.dto.UserProfileResponse.RecentSubmissionDto;
import com.pluto.userprofileservice.dto.UserProfileResponse.SolvedStatsDto;
import com.pluto.userprofileservice.dto.UserProfileResponse.StreakStatsDto;
import com.pluto.userprofileservice.dto.UserProfileResponse.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final ServiceClient serviceClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public UserProfileResponse getUserProfile(String username) {
        // 1. Fetch User details
        UserDto userDto = serviceClient.getUserByUsername(username);
        String userIdStr = String.valueOf(userDto.getUserId());

        // 2. Fetch submissions and problems
        List<SubmissionDto> submissions = serviceClient.getSubmissionsByUserId(userIdStr);
        List<ProblemDto> problems = serviceClient.getAllProblems();

        // Map problems by ID for quick lookup
        Map<UUID, ProblemDto> problemMap = problems.stream()
                .collect(Collectors.toMap(ProblemDto::getId, p -> p));

        // 3. Compute Solved Stats
        SolvedStatsDto solvedStats = calculateSolvedStats(submissions, problems, problemMap);

        // 4. Compute Submission Calendar (last 365 days)
        Map<String, Integer> calendar = calculateSubmissionCalendar(submissions);

        // 5. Compute Streak Statistics
        List<LocalDateTime> submissionDateTimes = submissions.stream()
                .map(SubmissionDto::getCreatedAt)
                .collect(Collectors.toList());
        StreakStatsDto streakStats = calculateStreak(submissionDateTimes);

        // 6. Format Recent Submissions (Top 15 accepted submissions)
        List<RecentSubmissionDto> recentSubmissions = submissions.stream()
                .filter(s -> "COMPLETED".equalsIgnoreCase(s.getStatus()))
                .limit(15)
                .map(s -> {
                    ProblemDto problem = problemMap.get(s.getProblemId());
                    return RecentSubmissionDto.builder()
                            .id(s.getId().toString())
                            .problemId(s.getProblemId().toString())
                            .problemTitle(problem != null ? problem.getTitle() : "Unknown Problem")
                            .problemSlug(problem != null ? problem.getSlug() : "unknown-problem")
                            .difficulty(problem != null ? problem.getDifficulty() : "UNKNOWN")
                            .status(s.getStatus())
                            .createdAt(s.getCreatedAt().format(ISO_FORMATTER))
                            .build();
                })
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .user(UserInfoDto.builder()
                        .username(userDto.getUsername())
                        .createdAt(userDto.getCreatedAt())
                        .build())
                .solvedStats(solvedStats)
                .submissionCalendar(calendar)
                .streakStats(streakStats)
                .recentSubmissions(recentSubmissions)
                .build();
    }

    private SolvedStatsDto calculateSolvedStats(
            List<SubmissionDto> submissions,
            List<ProblemDto> problems,
            Map<UUID, ProblemDto> problemMap) {

        // Total problems counts in system
        int easyTotal = 0;
        int mediumTotal = 0;
        int hardTotal = 0;

        for (ProblemDto p : problems) {
            String diff = p.getDifficulty().toUpperCase();
            if ("EASY".equals(diff)) easyTotal++;
            else if ("MEDIUM".equals(diff)) mediumTotal++;
            else if ("HARD".equals(diff)) hardTotal++;
        }

        // Solved and Attempted problem trackers
        Set<UUID> solvedProblemIds = new HashSet<>();
        Set<UUID> attemptedProblemIds = new HashSet<>();

        for (SubmissionDto s : submissions) {
            attemptedProblemIds.add(s.getProblemId());
            if ("COMPLETED".equalsIgnoreCase(s.getStatus())) {
                solvedProblemIds.add(s.getProblemId());
            }
        }

        int easySolved = 0;
        int mediumSolved = 0;
        int hardSolved = 0;

        for (UUID problemId : solvedProblemIds) {
            ProblemDto problem = problemMap.get(problemId);
            if (problem != null) {
                String diff = problem.getDifficulty().toUpperCase();
                if ("EASY".equals(diff)) easySolved++;
                else if ("MEDIUM".equals(diff)) mediumSolved++;
                else if ("HARD".equals(diff)) hardSolved++;
            }
        }

        int attemptingCount = attemptedProblemIds.size() - solvedProblemIds.size();

        return SolvedStatsDto.builder()
                .solvedCount(solvedProblemIds.size())
                .totalCount(problems.size())
                .easySolved(easySolved)
                .easyTotal(easyTotal)
                .mediumSolved(mediumSolved)
                .mediumTotal(mediumTotal)
                .hardSolved(hardSolved)
                .hardTotal(hardTotal)
                .attemptingCount(attemptingCount)
                .build();
    }

    private Map<String, Integer> calculateSubmissionCalendar(List<SubmissionDto> submissions) {
        Map<String, Integer> calendar = new HashMap<>();
        LocalDate oneYearAgo = LocalDate.now().minusDays(365);

        for (SubmissionDto s : submissions) {
            LocalDate subDate = s.getCreatedAt().toLocalDate();
            if (subDate.isAfter(oneYearAgo) || subDate.isEqual(oneYearAgo)) {
                String dateStr = subDate.format(DATE_FORMATTER);
                calendar.put(dateStr, calendar.getOrDefault(dateStr, 0) + 1);
            }
        }

        return calendar;
    }

    private StreakStatsDto calculateStreak(List<LocalDateTime> dateTimes) {
        if (dateTimes == null || dateTimes.isEmpty()) {
            return new StreakStatsDto(0, 0, 0);
        }

        // Get unique dates sorted ascending
        List<LocalDate> dates = dateTimes.stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        int maxStreak = 0;
        int tempStreak = 0;
        LocalDate prevDate = null;

        for (LocalDate date : dates) {
            if (prevDate == null) {
                tempStreak = 1;
            } else {
                long daysBetween = ChronoUnit.DAYS.between(prevDate, date);
                if (daysBetween == 1) {
                    tempStreak++;
                } else if (daysBetween > 1) {
                    if (tempStreak > maxStreak) {
                        maxStreak = tempStreak;
                    }
                    tempStreak = 1;
                }
            }
            prevDate = date;
        }
        if (tempStreak > maxStreak) {
            maxStreak = tempStreak;
        }

        // Calculate current streak (ends today or yesterday)
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        int currentStreak = 0;

        if (dates.contains(today) || dates.contains(yesterday)) {
            LocalDate checkDate = dates.contains(today) ? today : yesterday;
            while (dates.contains(checkDate)) {
                currentStreak++;
                checkDate = checkDate.minusDays(1);
            }
        }

        return new StreakStatsDto(dates.size(), maxStreak, currentStreak);
    }
}
