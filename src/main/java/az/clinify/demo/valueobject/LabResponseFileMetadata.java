package az.clinify.demo.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResponseFileMetadata {
    private String publicId;
    private String secureUrl;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String resourceType;
}
