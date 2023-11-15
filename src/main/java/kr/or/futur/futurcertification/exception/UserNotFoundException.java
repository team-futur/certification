package kr.or.futur.futurcertification.exception;

/**
 * 사용자 정보를 조회하지 못했을 때의 Exception
 */
public class UserNotFoundException extends RuntimeException {
    private static final String errorMessage = "사용자를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(errorMessage);
    }

    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
