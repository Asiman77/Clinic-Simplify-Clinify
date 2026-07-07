package az.clinify.demo.exceptions;

public class LabResponseNotFoundException extends RuntimeException
{
    public LabResponseNotFoundException(String message) {
        super(message);
    }
}
