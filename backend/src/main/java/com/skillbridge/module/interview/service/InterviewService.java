package com.skillbridge.module.interview.service;

import com.skillbridge.module.interview.dto.*;
import java.util.List;

public interface InterviewService {
    MockInterviewResponse bookSlot(Long studentUserId, BookSlotRequest request);
    List<MockInterviewResponse> getMyInterviews(Long studentUserId);
    List<MockInterviewResponse> getOfficerInterviews(Long officerUserId);
    MockInterviewResponse submitFeedback(Long interviewId, InterviewFeedbackRequest request);
}
