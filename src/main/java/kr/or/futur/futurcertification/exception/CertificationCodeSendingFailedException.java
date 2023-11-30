package kr.or.futur.futurcertification.exception;

/**
 * 인증번호 전송 실패 에러
 */
public class CertificationCodeSendingFailedException extends RuntimeException {

    private static String errorMessage = "인증번호를 전송하지 못했습니다.";

    public CertificationCodeSendingFailedException() {
        super(errorMessage);
    }

    public CertificationCodeSendingFailedException(String errorMessage) {
        super(errorMessage);
    }

    public CertificationCodeSendingFailedException(Throwable cause) {
        super(errorMessage, cause);
    }

    public CertificationCodeSendingFailedException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
