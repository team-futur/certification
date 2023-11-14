package kr.or.futur.futurcertification.service;

import kr.or.futur.futurcertification.domain.dto.request.ConfirmCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SendCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SignUpRequestDTO;
import kr.or.futur.futurcertification.domain.dto.response.ConfirmCertificationResponseDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignInResultDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignUpResultDTO;

public interface SignService {
    /**
     * 회원가입
     * @param signUpRequestDTO
     * @return
     */
    SignUpResultDTO signUp(SignUpRequestDTO signUpRequestDTO);


    /**
     * 로그인
     * @param id
     * @param password
     * @return
     * @throws RuntimeException
     */
    SignInResultDTO signIn(String id, String password) throws RuntimeException;

    /**
     * 인증번호 발송
     * @param sendCertificationRequestDTO
     */
    void sendCertificationNumber(SendCertificationRequestDTO sendCertificationRequestDTO);

    /**
     * 회원가입에서 인증
     * @param certificationRequestDTO
     * @return
     */
    boolean confirmCertificationNumber(ConfirmCertificationRequestDTO certificationRequestDTO);
}
