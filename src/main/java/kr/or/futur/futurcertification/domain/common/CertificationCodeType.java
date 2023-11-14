package kr.or.futur.futurcertification.domain.common;

/**
 * 인증번호 타입
 */
public enum CertificationCodeType {
    REGISTER("register"),
    FIND_ID("find-id"),
    FIND_PW("find-pw");

    private String val;

    CertificationCodeType(String val) {
        this.val = val;
    }
}
