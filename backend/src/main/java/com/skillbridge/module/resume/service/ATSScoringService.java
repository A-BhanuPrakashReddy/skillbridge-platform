package com.skillbridge.module.resume.service;

import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.module.resume.dto.ATSFeedbackDTO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ATSScoringService {

    private static final List<String> TECH_KEYWORDS = Arrays.asList(
        "java","python","c++","javascript","typescript","react","angular","vue",
        "spring","spring boot","node","express","html","css","tailwind","bootstrap",
        "sql","mysql","postgresql","mongodb","redis","aws","azure","gcp",
        "docker","kubernetes","git","github","linux","rest api","graphql",
        "machine learning","tensorflow","pandas","numpy","scikit",
        "junit","selenium","postman","jira","agile","scrum",
        "hibernate","jpa","maven","gradle","jenkins","ci/cd"
    );

    private static final List<String> ACTION_VERBS = Arrays.asList(
        "developed","built","implemented","designed","created","optimized","managed",
        "led","deployed","improved","automated","integrated","analyzed","delivered",
        "maintained","tested","debugged","refactored","architected","collaborated"
    );

    private static final Map<String, String> SECTION_PATTERNS = new LinkedHashMap<>();
    static {
        SECTION_PATTERNS.put("Education", "education|academic|degree|university|college");
        SECTION_PATTERNS.put("Experience", "experience|internship|work|employment|company");
        SECTION_PATTERNS.put("Projects", "project|built|developed|implemented|created");
        SECTION_PATTERNS.put("Skills", "skill|technolog|framework|language|tool");
        SECTION_PATTERNS.put("Certifications", "certif");
        SECTION_PATTERNS.put("Achievements", "achievement|award|honor|recogni|winner");
        SECTION_PATTERNS.put("Summary", "objective|summary|about|profile|career");
    }

    public String extractTextFromPDF(byte[] pdfBytes) {
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } catch (IOException e) {
            throw new BadRequestException("Cannot read PDF: " + e.getMessage());
        }
    }

    public ATSFeedbackDTO analyze(String pdfText, List<String> studentSkills) {
        String text = pdfText.toLowerCase();

        // 1. Sections score (0-30)
        List<String> foundSections = new ArrayList<>(), missingSections = new ArrayList<>();
        for (Map.Entry<String, String> entry : SECTION_PATTERNS.entrySet()) {
            if (Pattern.compile(entry.getValue()).matcher(text).find()) foundSections.add(entry.getKey());
            else missingSections.add(entry.getKey());
        }
        int sectionsScore = (int) Math.round((double) foundSections.size() / SECTION_PATTERNS.size() * 30);

        // 2. Skills score (0-40)
        List<String> present = new ArrayList<>(), missing = new ArrayList<>();
        for (String kw : TECH_KEYWORDS) {
            if (text.contains(kw)) present.add(kw);
            else missing.add(kw);
        }
        int skillsScore = (int) Math.round((double) present.size() / TECH_KEYWORDS.size() * 40);
        List<String> missingTop10 = missing.stream().limit(10).collect(Collectors.toList());

        // 3. Formatting score (0-20)
        int formattingScore = 0;
        if (Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}").matcher(text).find()) formattingScore += 5;
        if (Pattern.compile("\\d{10}").matcher(text).find()) formattingScore += 5;
        if (text.contains("github.com")) formattingScore += 5;
        if (text.contains("linkedin.com")) formattingScore += 5;

        // 4. Action verbs score (0-10)
        long verbsFound = ACTION_VERBS.stream().filter(text::contains).count();
        int actionVerbsScore = (int) Math.min(verbsFound, 10);

        int total = sectionsScore + skillsScore + formattingScore + actionVerbsScore;

        // Build suggestions and strengths
        List<String> suggestions = new ArrayList<>(), strengths = new ArrayList<>();
        if (!missingSections.isEmpty()) suggestions.add("Add missing sections: " + String.join(", ", missingSections));
        if (!missingTop10.isEmpty()) suggestions.add("Include key skills: " + String.join(", ", missingTop10.subList(0, Math.min(5, missingTop10.size()))));
        if (formattingScore < 20) suggestions.add("Add GitHub/LinkedIn links, email, and phone number");
        if (actionVerbsScore < 5) suggestions.add("Use more action verbs (developed, implemented, designed...)");
        if (!foundSections.isEmpty()) strengths.add("Resume contains: " + String.join(", ", foundSections));
        if (!present.isEmpty()) strengths.add("Good skills coverage: " + String.join(", ", present.subList(0, Math.min(5, present.size()))));

        return ATSFeedbackDTO.builder()
                .presentSkills(present).missingSkills(missingTop10).missingSections(missingSections)
                .suggestions(suggestions).strengths(strengths)
                .sectionsScore(sectionsScore).skillsScore(skillsScore)
                .formattingScore(formattingScore).actionVerbsScore(actionVerbsScore)
                .totalScore(total).build();
    }
}
