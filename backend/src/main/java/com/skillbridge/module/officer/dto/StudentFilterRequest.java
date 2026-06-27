package com.skillbridge.module.officer.dto;

import lombok.Data;

@Data
public class StudentFilterRequest {
    private String department;
    private Double minCgpa;
    private Double maxCgpa;
    private Integer maxBacklogs;
    private Double minReadinessScore;
    private String skill;
    private Integer page = 0;
    private Integer size = 20;
}
