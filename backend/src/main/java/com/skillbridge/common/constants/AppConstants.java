package com.skillbridge.common.constants;

public interface AppConstants {
    int DEFAULT_PAGE_SIZE = 10;
    int MAX_PAGE_SIZE = 50;

    double DSA_WEIGHT = 0.30;
    double APTITUDE_WEIGHT = 0.25;
    double RESUME_WEIGHT = 0.20;
    double PROFILE_WEIGHT = 0.15;
    double INTERVIEW_WEIGHT = 0.10;

    double STRONG_THRESHOLD = 70.0;
    double AVERAGE_THRESHOLD = 40.0;

    int DSA_WEAK_TOPIC_THRESHOLD = 3;
    double ELIGIBILITY_SKILL_THRESHOLD = 0.6;
    double DSA_ELIGIBILITY_THRESHOLD = 30.0;

    String ROLE_STUDENT = "STUDENT";
    String ROLE_OFFICER = "PLACEMENT_OFFICER";
    String ROLE_ADMIN = "ADMIN";
}
