package com.skillbridge.module.resume.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.exception.UnauthorizedException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.auth.repository.UserRepository;
import com.skillbridge.module.readiness.service.ReadinessScoreService;
import com.skillbridge.module.resume.dto.ATSFeedbackDTO;
import com.skillbridge.module.resume.dto.ResumeUploadResponse;
import com.skillbridge.module.resume.entity.ResumeUpload;
import com.skillbridge.module.resume.repository.ResumeUploadRepository;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeUploadRepository resumeRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;
    private final ATSScoringService atsScoringService;
    private final ReadinessScoreService readinessScoreService;
    private final ObjectMapper objectMapper;

    @Override @Transactional
    public ApiResponse<ResumeUploadResponse> uploadResume(Long userId, MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("File is empty");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf"))
            throw new BadRequestException("Only PDF files are allowed");
        if (file.getSize() > 10 * 1024 * 1024)
            throw new BadRequestException("File size exceeds 10MB");

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        resumeRepository.markAllNotLatestForUser(userId);

        Map uploadResult = cloudinaryService.upload(file, "skillbridge/resumes", "raw");
        String cloudinaryUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        String pdfText;
        ATSFeedbackDTO feedback;
        try {
            pdfText = atsScoringService.extractTextFromPDF(file.getBytes());
            List<String> skills = profileRepository.findByUserIdAndIsDeletedFalse(userId)
                    .map(p -> p.getSkillsList()).orElse(new ArrayList<>());
            feedback = atsScoringService.analyze(pdfText, skills);
        } catch (Exception e) {
            feedback = ATSFeedbackDTO.builder().totalScore(0).sectionsScore(0)
                    .skillsScore(0).formattingScore(0).actionVerbsScore(0)
                    .suggestions(List.of("Could not analyze PDF")).build();
        }

        String feedbackJson;
        try { feedbackJson = objectMapper.writeValueAsString(feedback); }
        catch (Exception e) { feedbackJson = "{}"; }

        int version = (int) resumeRepository.countByUserId(userId) + 1;
        ResumeUpload resume = ResumeUpload.builder()
                .user(user).fileName(file.getOriginalFilename())
                .cloudinaryUrl(cloudinaryUrl).cloudinaryPublicId(publicId)
                .atsScore(BigDecimal.valueOf(feedback.getTotalScore()))
                .atsFeedback(feedbackJson).versionNumber(version)
                .isLatest(true).uploadedAt(LocalDateTime.now()).build();
        resumeRepository.save(resume);
        readinessScoreService.computeAndSave(userId);
        return ApiResponse.success("Resume uploaded", toResponse(resume, feedback));
    }

    @Override
    public ApiResponse<List<ResumeUploadResponse>> getMyResumes(Long userId) {
        List<ResumeUpload> resumes = resumeRepository.findByUserIdOrderByUploadedAtDesc(userId);
        return ApiResponse.success("Resumes fetched", resumes.stream().map(r -> toResponse(r, parseFeedback(r))).collect(Collectors.toList()));
    }

    @Override
    public ApiResponse<ResumeUploadResponse> getLatestResume(Long userId) {
        ResumeUpload resume = resumeRepository.findByUserIdAndIsLatestTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No resume found"));
        return ApiResponse.success("Latest resume", toResponse(resume, parseFeedback(resume)));
    }

    @Override @Transactional
    public ApiResponse<Void> deleteResume(Long userId, Long resumeId) {
        ResumeUpload resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        if (!resume.getUser().getId().equals(userId))
            throw new UnauthorizedException("Not authorized to delete this resume");
        cloudinaryService.delete(resume.getCloudinaryPublicId(), "raw");
        resumeRepository.delete(resume);
        return ApiResponse.success("Resume deleted");
    }

    private ATSFeedbackDTO parseFeedback(ResumeUpload r) {
        try { return objectMapper.readValue(r.getAtsFeedback(), ATSFeedbackDTO.class); }
        catch (Exception e) { return null; }
    }

    private ResumeUploadResponse toResponse(ResumeUpload r, ATSFeedbackDTO feedback) {
        return ResumeUploadResponse.builder().id(r.getId()).fileName(r.getFileName())
                .cloudinaryUrl(r.getCloudinaryUrl()).atsScore(r.getAtsScore())
                .feedback(feedback).versionNumber(r.getVersionNumber())
                .uploadedAt(r.getUploadedAt()).build();
    }
}
