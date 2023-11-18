package kr.or.futur.futurcertification.service;

import kr.or.futur.futurcertification.domain.dto.UserDTO;
import kr.or.futur.futurcertification.domain.dto.request.ConfirmCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SendCertificationRequestDTO;
import kr.or.futur.futurcertification.domain.dto.request.SignUpRequestDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignInResultDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignUpResultDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    /**
     * 사용자 삭제
     * @param userId
     */
    void deleteUser(String userId);


    /**
     * 삭제된 유저를 복구
     * @param userId
     */
    void restoreDeletedUser(String userId);

    /**
     * 아이디 찾기
     * @param userId
     * @return
     */
    UserDTO findUserId(String userId);

    /**
     * 전화번호로 사용자 찾기
     * @param phoneNumber
     * @return
     */
    UserDTO findPhoneNumber(String phoneNumber);

    /**
     * 사용자 데이터 가져오기
     *
     * @param pageable
     * @param delYn
     * @return
     */
    List<UserDTO> findAllByDelYn(Pageable pageable, boolean delYn);

}
