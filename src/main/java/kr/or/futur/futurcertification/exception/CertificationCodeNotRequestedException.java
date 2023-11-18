package kr.or.futur.futurcertification.exception;

/**
 * 인증 번호를 요청하지 않았음을 나타내는 에러
 */
public class CertificationCodeNotRequestedException extends RuntimeException {

    private static final String errorMessage = "인증 번호를 요청해주세요. 요청 시간이 초과하여 만료되었을 수 있습니다.";

    public CertificationCodeNotRequestedException() {
        super(errorMessage);
    }

    public CertificationCodeNotRequestedException(String errorMessage) {
        super(errorMessage);
    }

    public CertificationCodeNotRequestedException(Throwable cause) {
        super(errorMessage, cause);
    }

    public CertificationCodeNotRequestedException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
