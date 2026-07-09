package com.pluto.submissionservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "submissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "problem_id", nullable = false)
    private UUID problemId;

    @Column(name = "excalidraw_json_url")
    private String excalidrawJsonUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String writeup;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, FAILED

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "parsed_diagram", columnDefinition = "TEXT")
    private String parsedDiagram;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
