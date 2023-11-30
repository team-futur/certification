package kr.or.futur.futurcertification.exception;

public class DeleteFailedException extends RuntimeException {

    private static final String errorMessage = "사용자를 삭제하지 못했습니다.";

    public DeleteFailedException() {
        super(errorMessage);
    }


    public DeleteFailedException(String message) {
        super(message);
    }

    public DeleteFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteFailedException(Throwable cause) {
        super(cause);
    }
}
