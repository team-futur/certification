package kr.or.futur.futurcertification.exception;

/**
 * 인증번호가 만료되었을 때의 Exception
 */
public class CertificationCodeExpiredException extends RuntimeException {
    private static String errorMessage = "인증번호가 만료되었습니다.";

    public CertificationCodeExpiredException() {
        super(errorMessage);
    }

    public CertificationCodeExpiredException(String errorMessage) {
        super(errorMessage);
    }
}
