package com.skillbridge.module.resume.service;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.resume.dto.ResumeUploadResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ResumeService {
    ApiResponse<ResumeUploadResponse> uploadResume(Long userId, MultipartFile file);
    ApiResponse<List<ResumeUploadResponse>> getMyResumes(Long userId);
    ApiResponse<ResumeUploadResponse> getLatestResume(Long userId);
    ApiResponse<Void> deleteResume(Long userId, Long resumeId);
}
