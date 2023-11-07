package kr.or.futur.futurcertification.exception;

/**
 * 중복된 사용자 아이디가 있을 경우
 */
public class DuplicateUserIdException extends RuntimeException{
    public DuplicateUserIdException(String errorMessage) {
        super(errorMessage);
    }
}
