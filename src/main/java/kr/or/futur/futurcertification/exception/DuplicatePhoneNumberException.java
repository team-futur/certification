package kr.or.futur.futurcertification.exception;

public class DuplicatePhoneNumberException extends RuntimeException {

    private static final String errorMessage = "등록된 휴대전화 번호가 있습니다.";

    public DuplicatePhoneNumberException() {
        super(errorMessage);
    }

    public DuplicatePhoneNumberException(String errorMessage) {
        super(errorMessage);
    }
}
