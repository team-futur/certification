package kr.or.futur.futurcertification.service;

import kr.or.futur.futurcertification.domain.dto.response.SignInResultDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignUpResultDTO;

public interface SignService {
    /**
     * 회원가입
     * @param id
     * @param password
     * @param name
     * @param role
     * @return
     */
    SignUpResultDTO signUp(String id, String password, String name, String role);


    /**
     * 로그인
     * @param id
     * @param password
     * @return
     * @throws RuntimeException
     */
    SignInResultDTO signIn(String id, String password) throws RuntimeException;
}
