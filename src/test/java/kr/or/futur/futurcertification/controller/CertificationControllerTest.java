package kr.or.futur.futurcertification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.futur.futurcertification.domain.common.CertificationCodeType;
import kr.or.futur.futurcertification.repository.UserRepository;
import kr.or.futur.futurcertification.service.RedisService;
import kr.or.futur.futurcertification.service.SignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CertificationControllerTest {

    /* TODO @Order 어노테이션이 정상적으로 동작하는지 확인 필요 */

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SignService signService;

    @Autowired
    private UserRepository userRepository;

    private static final String phoneNumber = "010-6526-3863";
//    private static final String phoneNumber = "010-2355-7934";

    private final static Logger log = LoggerFactory.getLogger(CertificationControllerTest.class);

    /**
     * 인증번호 요청(공통)
     * @param inputs 파라미터
     * @throws Exception
     */
    void requestCertificationCode(Map<String, Object> inputs) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/certification/request-certification-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * 인증번호 확인(공통)
     * @param inputs
     * @throws Exception
     */
    void confirmCertificationCode(Map<String, Object> inputs) throws Exception {
        /* 인증번호 확인 */
        mockMvc.perform(MockMvcRequestBuilders.put("/certification/confirm-certification-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("유레카 서버 연결 확인")
    void connected() throws Exception {
        Map<String, String> input = new HashMap<>();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/certification/connected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("connected-test",
                        responseFields(
                                fieldWithPath("isConnected").description("유레카 서버 연결 여부")
                        )
                ))
                .andExpect(jsonPath("$.isConnected").value(is(true)));
    }

    @Test
    @Transactional
    @DisplayName("회원가입")
    void signUp() throws Exception {
        Map<String, Object> inputs = new HashMap<>();
        Map<String, Object> certificationCodeInputs = new HashMap<>();

        inputs.put("id", "test" + Math.round(Math.random() * 10000));
        inputs.put("password", "@Aa1234567890");
        inputs.put("name", "test");
        inputs.put("role", "ROLE_USER");
        inputs.put("email", "test@test.com");
        inputs.put("phoneNumber", "010-6526-3863");
        inputs.put("address", "test");
        inputs.put("birthDay", "2022-01-01");

        certificationCodeInputs.put("phoneNumber", "010-6526-3863");
        certificationCodeInputs.put("type", CertificationCodeType.REGISTER.name());

        /* 휴대폰 번호 등록된 사람 제거 */
        userRepository.findByPhoneNumber("010-6526-3863")
                .ifPresent(user -> {
                    user.setDelYn(true);
                    userRepository.save(user);
                });

        /* 인증번호 요청 */
        requestCertificationCode(certificationCodeInputs);

        /* Redis에서 정보 가져오기 */
        String certificationNumber = (String) redisService.getData(CertificationCodeType.REGISTER.name() + "_" + phoneNumber);
        certificationCodeInputs.put("certificationNumber", certificationNumber);

        /* 인증번호 확인 */
        confirmCertificationCode(certificationCodeInputs);

        /* 회원가입 요청 */
        mockMvc.perform(RestDocumentationRequestBuilders.post("/certification/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("sign-up-test",
                        requestFields(
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("role").description("권한"),
                                fieldWithPath("email").description("이메일").optional(),
                                fieldWithPath("phoneNumber").description("비밀번호"),
                                fieldWithPath("address").description("주소").optional(),
                                fieldWithPath("birthDay").description("날짜")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공여부"),
                                fieldWithPath("code").description("코드"),
                                fieldWithPath("msg").description("메세지"),
                                fieldWithPath("data").description("반환 데이터")
                        )
                ))
                .andExpect(jsonPath("$.success").value(is(true)))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.msg").value(is("회원가입을 성공했습니다.")));
    }

    @Test
    @Transactional
    @DisplayName("로그인")
    void signIn() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("userId", "test12345");
        inputs.put("password", "test");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/certification/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("sign-in-test",
                        requestFields(
                                fieldWithPath("userId").description("아이디"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("data").description("반환 데이터"),
                                fieldWithPath("data.token").description("토큰(1일)"),
                                fieldWithPath("data.refreshToken").description("리프레시 토큰(30일)"),
                                fieldWithPath("code").description("상태여부"),
                                fieldWithPath("msg").description("메세지")
                        )
                ))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.msg").value(is("로그인을 성공했습니다.")))
                .andExpect(jsonPath("$.success").value(is(true)))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("인증번호 요청")
    void requestCertificationNumber() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("phoneNumber", phoneNumber);
        inputs.put("type", CertificationCodeType.REGISTER.name());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/certification/request-certification-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("request-certification-number-test",
                                requestFields(
                                        fieldWithPath("phoneNumber").description("휴대전화 번호"),
                                        fieldWithPath("type").description("인증번호 타입(REGISTER/FIND_ID/FIND_PW)")
                                )
                        )
                );
    }

    @Test
    @DisplayName("인증번호 확인")
    void confirmCertificationNumber() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("phoneNumber", phoneNumber);
        inputs.put("type", CertificationCodeType.REGISTER.name());

        /* 인증번호 발송 */
        requestCertificationCode(inputs);

        /* Redis에서 정보 가져오기 */
        String certificationNumber = (String) redisService.getData(CertificationCodeType.REGISTER.name() + "_" + phoneNumber);
        inputs.put("certificationNumber", certificationNumber);

        /* 인증번호 확인 */
        mockMvc.perform(RestDocumentationRequestBuilders.put("/certification/confirm-certification-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("confirm-certification-number-test",
                        requestFields(
                                fieldWithPath("certificationNumber").description("인증번호"),
                                fieldWithPath("type").description("인증번호 타입(REGISTER/FIND_ID/FIND_PW)"),
                                fieldWithPath("phoneNumber").description("휴대전화 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("data").description("반환 데이터"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("msg").description("메세지")
                        )
                ))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.msg").value(is("인증번호 확인에 성공")))
                .andExpect(jsonPath("$.success").value(is(true)));
    }

    @Test
    @Transactional
    @DisplayName("사용자 삭제")
    void deleteUser() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        String userId = "test12345";

        inputs.put("phoneNumber", phoneNumber);
        inputs.put("type", CertificationCodeType.DELETE.name());

        /* 인증번호 발송 */
        requestCertificationCode(inputs);

        /* Redis에서 정보 가져오기 */
        String certificationNumber = (String) redisService.getData(CertificationCodeType.DELETE.name() + "_" + phoneNumber);
        inputs.put("certificationNumber", certificationNumber);

        /* 인증번호 확인 */
        confirmCertificationCode(inputs);

        /* 사용자 삭제 */
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/certification/{userId}", userId))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-user-id",
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("msg").description("메세지"),
                                fieldWithPath("data").description("반환 데이터"),
                                fieldWithPath("code").description("응답 코드")
                        )
                        ))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.msg").value(is(userId + " 사용자를 삭제하였습니다.")))
                .andExpect(jsonPath("$.success").value(is(true)));
    }

    @Test
    @Transactional
    @DisplayName("삭제된 회원 복구")
    void restoreUser() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("phoneNumber", phoneNumber);
        inputs.put("type", CertificationCodeType.RESTORE.name());

        String userId = "test11111";

        /* 인증번호 요청 */
        requestCertificationCode(inputs);

        /* Redis에서 정보 가져오기 */
        String certificationNumber = (String) redisService.getData((CertificationCodeType.RESTORE.name() + "_" + phoneNumber));
        inputs.put("certificationNumber", certificationNumber);

        /* 인증번호 확인 */
        confirmCertificationCode(inputs);

        /* 아이디 복구 */
        mockMvc.perform(RestDocumentationRequestBuilders.put("/certification/restore/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("restore-user-id",
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("msg").description("메세지"),
                                fieldWithPath("data").description("반환 데이터"),
                                fieldWithPath("code").description("응답 코드")
                        )
                ))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.msg").value(is(userId + " 사용자를 복구하였습니다.")))
                .andExpect(jsonPath("$.success").value(is(true)));
    }

    @Test
    @DisplayName("사용자 조회")
    void findUserIdAndPhoneNumber() throws Exception {
        String userIdAndPhoneNumber = "010-6526-3863";

        mockMvc.perform(RestDocumentationRequestBuilders.get("/certification/{userIdAndPhoneNumber}", userIdAndPhoneNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-user-id-and-phone-number-test",
                        pathParameters(
                                parameterWithName("userIdAndPhoneNumber").description("사용자 ID 또는 휴대전화 번호")
                        )
                ))
                .andExpect(jsonPath("$.data.userId").value(is("test12345")))
                .andExpect(jsonPath("$.data.phoneNumber").value(is("010-6526-3863")))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.success").value(is(true)));
    }

    @Test
    @DisplayName("아이디 중복 체크")
    void isDuplicateUserId() throws Exception {
        String userId = "test12344";

        mockMvc.perform(RestDocumentationRequestBuilders.post("/certification/duplicate/{userId}", userId))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("is-duplicate-user-id-test"))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.msg").value(is("중복된 아이디가 존재하지 않습니다.")))
                .andExpect(jsonPath("$.success").value(is(true)));
    }

    @Test
    @DisplayName("사용자 아이디 찾기")
    void findUserId() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("type", CertificationCodeType.FIND_ID);
        inputs.put("phoneNumber", phoneNumber);
        inputs.put("userId", "test12345");

        /* 인증번호 요청 */
        requestCertificationCode(inputs);

        String certificationNumber = (String) redisService.getData(CertificationCodeType.FIND_ID.name() + "_" + phoneNumber);
        inputs.put("certificationNumber", certificationNumber);

        /* 인증번호 확인 */
        confirmCertificationCode(inputs);

        /* 서버 요청 */
        mockMvc.perform(RestDocumentationRequestBuilders.get("/certification/find-lost-user-id")
                        .param("type", CertificationCodeType.FIND_ID.name())
                        .param("phoneNumber", phoneNumber)
                        .param("userId", "test12345"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-user-id-test",
                        requestParameters(
                                parameterWithName("userId").description("아이디"),
                                parameterWithName("type").description("인증번호 타입(REGISTER/FIND_ID/FIND_PW)"),
                                parameterWithName("phoneNumber").description("휴대전화 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("data").description("반환 데이터"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("msg").description("메세지"),
                                fieldWithPath("data.userId").description("일부 사용자 아이디")
                        )
                ))
                .andExpect(jsonPath("$.success").value(is(true)))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.data.userId").value(is("test1***")));
    }
}