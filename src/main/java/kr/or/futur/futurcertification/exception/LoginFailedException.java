package kr.or.futur.futurcertification.exception;

public class LoginFailedException extends RuntimeException {

    public static final String errorMessage = "로그인을 하지 못했습니다.";

    public LoginFailedException() {
        super(errorMessage);
    }

    public LoginFailedException(String errorMessage) {
        super(errorMessage);
    }
}
