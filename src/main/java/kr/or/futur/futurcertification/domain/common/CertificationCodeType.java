package kr.or.futur.futurcertification.domain.common;

/**
 * 인증번호 타입
 */
public enum CertificationCodeType {
    /* 회원 가입 */
    REGISTER,

    /* 아이디 찾기 */
    FIND_ID,

    /* 비밀 번호 찾기 */
    FIND_PW,

    /* 사용자 삭제 */
    DELETE,

    /* 사용자 복구 */
    RESTORE
}
