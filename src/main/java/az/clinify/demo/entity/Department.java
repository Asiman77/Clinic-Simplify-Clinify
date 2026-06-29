package az.clinify.demo.entity;

import jakarta.persistence.Column;

public class Department extends BaseEntity{

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;
}
