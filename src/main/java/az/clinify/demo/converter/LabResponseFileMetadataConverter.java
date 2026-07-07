package az.clinify.demo.converter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import az.clinify.demo.valueobject.LabResponseFileMetadata;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LabResponseFileMetadataConverter implements AttributeConverter<List<LabResponseFileMetadata>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<LabResponseFileMetadata> files) {
        if (files == null || files.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(files);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not convert lab response files to JSON", e);
        }
    }

    @Override
    public List<LabResponseFileMetadata> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<LabResponseFileMetadata>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not convert JSON to lab response files", e);
        }
    }
}