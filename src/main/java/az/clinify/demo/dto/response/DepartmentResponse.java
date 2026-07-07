package az.clinify.demo.dto.response;

import lombok.*;

@Getter
@Setter
public class DepartmentResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean active;

    public DepartmentResponse(Long id, String name, String description, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public DepartmentResponse() {

    }

}