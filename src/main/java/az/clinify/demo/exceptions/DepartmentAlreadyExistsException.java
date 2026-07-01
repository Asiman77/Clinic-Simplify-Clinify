package az.clinify.demo.exceptions;

public class DepartmentAlreadyExistsException extends RuntimeException {

    //This Exception will use when you create duplicate department with existed name

    public DepartmentAlreadyExistsException(String name) {
        super("Department already exists with name: " + name);
    }
}
