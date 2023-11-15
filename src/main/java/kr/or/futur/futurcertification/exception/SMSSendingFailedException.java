package kr.or.futur.futurcertification.exception;

/**
 * SMS 발송 실패 Exception
 */
public class SMSSendingFailedException extends RuntimeException {
    private static final String errorMessage = "SMS를 발송하지 못했습니다.";

    public SMSSendingFailedException() {
        super(errorMessage);
    }

    public SMSSendingFailedException(String errorMessage) {
        super(errorMessage);
    }
}
