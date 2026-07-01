package az.clinify.demo.repository;

import az.clinify.demo.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    //This methods we will use when update name of department , we cant take another's department's name as our new department name
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

}