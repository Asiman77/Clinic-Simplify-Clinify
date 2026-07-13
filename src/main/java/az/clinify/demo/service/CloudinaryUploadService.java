package az.clinify.demo.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import az.clinify.demo.exceptions.BaseBadRequestException;
import az.clinify.demo.valueobject.LabResponseFileMetadata;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadService {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg");

    private final Cloudinary cloudinary;

    public LabResponseFileMetadata uploadLabResponseFile(Long labResponseId, MultipartFile file) {
        validateFile(file);

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", "clinify/lab-responses/" + labResponseId,
                            "resource_type", "auto",
                            "public_id", buildPublicId(file)));

            return new LabResponseFileMetadata(
                    String.valueOf(uploadResult.get("public_id")),
                    String.valueOf(uploadResult.get("secure_url")),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    String.valueOf(uploadResult.get("resource_type")));
        } catch (IOException e) {
            throw new BaseBadRequestException("Could not upload lab response file");
        }
    }

    public void deleteLabResponseFile(
            LabResponseFileMetadata fileMetadata) {

        if (fileMetadata == null || fileMetadata.getPublicId() == null || fileMetadata.getPublicId().isBlank()) {
            throw new BaseBadRequestException(
                    "Lab response file metadata is invalid");
        }

        Map<String, Object> options = new HashMap<>();
        options.put("invalidate", true);

        if (fileMetadata.getResourceType() != null && !fileMetadata.getResourceType().isBlank()) {
            options.put("resource_type", fileMetadata.getResourceType());
        }

        try {
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(fileMetadata.getPublicId(), options);
            String result = String.valueOf(deleteResult.get("result"));

            if (!"ok".equals(result) && !"not found".equals(result)) {
                throw new BaseBadRequestException("Could not delete lab response file");
            }
        } catch (IOException exception) {
            throw new BaseBadRequestException(
                    "Could not delete lab response file");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseBadRequestException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BaseBadRequestException("File size must not exceed 10MB");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BaseBadRequestException("Only PDF, PNG and JPG files are allowed");
        }
    }

    private String buildPublicId(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        String safeName = originalFileName == null
                ? "lab-result"
                : originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        int dotIndex = safeName.lastIndexOf(".");
        if (dotIndex > 0) {
            safeName = safeName.substring(0, dotIndex);
        }

        return safeName + "-" + UUID.randomUUID();
    }
}
