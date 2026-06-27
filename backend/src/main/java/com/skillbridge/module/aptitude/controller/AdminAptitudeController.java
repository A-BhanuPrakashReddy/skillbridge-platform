package com.skillbridge.module.aptitude.controller;

import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.aptitude.dto.AptitudeQuestionRequest;
import com.skillbridge.module.aptitude.dto.AptitudeQuestionResponse;
import com.skillbridge.module.aptitude.entity.AptitudeQuestion;
import com.skillbridge.module.aptitude.repository.AptitudeQuestionRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin/aptitude")
@RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')") @Tag(name = "Admin - Aptitude")
public class AdminAptitudeController {
    private final AptitudeQuestionRepository questionRepository;

    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<AptitudeQuestionResponse>> addQuestion(@Valid @RequestBody AptitudeQuestionRequest req) {
        AptitudeQuestion q = AptitudeQuestion.builder()
                .questionText(req.getQuestionText()).optionA(req.getOptionA()).optionB(req.getOptionB())
                .optionC(req.getOptionC()).optionD(req.getOptionD()).correctOption(req.getCorrectOption())
                .category(req.getCategory()).difficulty(req.getDifficulty()).explanation(req.getExplanation()).build();
        questionRepository.save(q);
        return ResponseEntity.ok(ApiResponse.success("Question added", toResponse(q)));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<AptitudeQuestionResponse>> updateQuestion(@PathVariable Long id, @Valid @RequestBody AptitudeQuestionRequest req) {
        AptitudeQuestion q = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        q.setQuestionText(req.getQuestionText()); q.setOptionA(req.getOptionA()); q.setOptionB(req.getOptionB());
        q.setOptionC(req.getOptionC()); q.setOptionD(req.getOptionD()); q.setCorrectOption(req.getCorrectOption());
        q.setCategory(req.getCategory()); q.setDifficulty(req.getDifficulty()); q.setExplanation(req.getExplanation());
        questionRepository.save(q);
        return ResponseEntity.ok(ApiResponse.success("Question updated", toResponse(q)));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        AptitudeQuestion q = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        q.setDeleted(true); questionRepository.save(q);
        return ResponseEntity.ok(ApiResponse.success("Question deleted"));
    }

    private AptitudeQuestionResponse toResponse(AptitudeQuestion q) {
        return AptitudeQuestionResponse.builder().id(q.getId()).questionText(q.getQuestionText())
                .optionA(q.getOptionA()).optionB(q.getOptionB()).optionC(q.getOptionC()).optionD(q.getOptionD())
                .category(q.getCategory()).difficulty(q.getDifficulty()).build();
    }
}
