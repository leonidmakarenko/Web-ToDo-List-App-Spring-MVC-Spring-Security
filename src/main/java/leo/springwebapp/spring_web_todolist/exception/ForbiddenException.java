package leo.springwebapp.spring_web_todolist.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {    }
    public ForbiddenException(String message) {
        super(message);
    }
}