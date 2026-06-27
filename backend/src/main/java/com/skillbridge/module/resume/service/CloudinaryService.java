package com.skillbridge.module.resume.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.skillbridge.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service @RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public Map upload(MultipartFile file, String folder, String resourceType) {
        try {
            return cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", folder, "resource_type", resourceType));
        } catch (Exception e) {
            throw new BadRequestException("Upload failed: " + e.getMessage());
        }
    }

    public void delete(String publicId, String resourceType) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
        } catch (Exception e) {
            // log but don't fail
        }
    }
}
