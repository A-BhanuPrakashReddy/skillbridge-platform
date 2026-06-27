package com.skillbridge.module.interview.service;

import com.skillbridge.common.enums.InterviewStatus;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.module.interview.dto.*;
import com.skillbridge.module.interview.entity.MockInterview;
import com.skillbridge.module.interview.repository.MockInterviewRepository;
import com.skillbridge.module.student.entity.StudentProfile;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final MockInterviewRepository interviewRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Override
    public MockInterviewResponse bookSlot(Long studentUserId, BookSlotRequest request) {
        StudentProfile profile = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        MockInterview interview = MockInterview.builder()
                .student(profile)
                .scheduledAt(request.getScheduledAt())
                .status(InterviewStatus.PENDING)
                .interviewType(request.getInterviewType() != null ? request.getInterviewType() : "TECHNICAL")
                .build();

        return toResponse(interviewRepository.save(interview));
    }

    @Override
    public List<MockInterviewResponse> getMyInterviews(Long studentUserId) {
        return interviewRepository.findByStudentUserId(studentUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<MockInterviewResponse> getOfficerInterviews(Long officerUserId) {
        return interviewRepository.findByOfficerId(officerUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public MockInterviewResponse submitFeedback(Long interviewId, InterviewFeedbackRequest request) {
        MockInterview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        interview.setScore(request.getScore());
        interview.setFeedback(request.getFeedback());
        interview.setStatus(InterviewStatus.COMPLETED);
        return toResponse(interviewRepository.save(interview));
    }

    private MockInterviewResponse toResponse(MockInterview m) {
        return MockInterviewResponse.builder()
                .id(m.getId())
                .studentId(m.getStudent().getId())
                .studentName(m.getStudent().getUser().getName())
                .officerId(m.getOfficer() != null ? m.getOfficer().getId() : null)
                .officerName(m.getOfficer() != null ? m.getOfficer().getName() : null)
                .scheduledAt(m.getScheduledAt())
                .status(m.getStatus().name())
                .score(m.getScore())
                .feedback(m.getFeedback())
                .interviewType(m.getInterviewType())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
