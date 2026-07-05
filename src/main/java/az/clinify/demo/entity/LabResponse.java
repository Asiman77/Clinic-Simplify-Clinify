package az.clinify.demo.entity;

import java.util.ArrayList;
import java.util.List;

import az.clinify.demo.converter.LabResponseFileMetadataConverter;
import az.clinify.demo.enums.LabStatuses;
import az.clinify.demo.valueobject.LabResponseFileMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lab_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_technician_id")
    private User labTechnician;

    @Column(nullable = false)
    private String testName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LabStatuses status = LabStatuses.PENDING;

    @Column(columnDefinition = "TEXT")
    private String resultText;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Convert(converter = LabResponseFileMetadataConverter.class)
    @Column(name = "file_metadata", columnDefinition = "JSON")
    private List<LabResponseFileMetadata> files = new ArrayList<>();
}