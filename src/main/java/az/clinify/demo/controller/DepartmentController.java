package az.clinify.demo.controller;


import az.clinify.demo.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

    public DepartmentService departmentService;
}
