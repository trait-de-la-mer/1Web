import java.io.Serial;

class ValidationException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}