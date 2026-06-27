package com.skillbridge.module.student.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StudentProfileRequest {
    @Size(max = 200) private String college;
    @Size(max = 100) private String branch;
    @Min(2020) @Max(2035) private Integer graduationYear;
    @DecimalMin("0.0") @DecimalMax("10.0") private BigDecimal cgpa;
    @Min(0) private Integer activeBacklogs;
    @Min(0) private Integer totalBacklogs;
    private String phone;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private List<String> skills;
}
