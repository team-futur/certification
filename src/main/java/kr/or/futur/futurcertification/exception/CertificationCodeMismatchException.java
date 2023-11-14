package kr.or.futur.futurcertification.exception;

/**
 * 인증번호 불일치
 */
public class CertificationCodeMismatchException extends RuntimeException {
    private static String errorMessage = "인증번호가 불일치합니다.";

    public CertificationCodeMismatchException() {
        super(errorMessage);
    }

    public CertificationCodeMismatchException(String errorMessage) {
        super(errorMessage);
    }
}
