package kr.or.futur.futurcertification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.futur.futurcertification.domain.common.CertificationCodeType;
import kr.or.futur.futurcertification.domain.dto.UserDTO;
import kr.or.futur.futurcertification.domain.dto.request.SignUpRequestDTO;
import kr.or.futur.futurcertification.domain.dto.response.SignUpResultDTO;
import kr.or.futur.futurcertification.domain.entity.User;
import kr.or.futur.futurcertification.exception.UserNotFoundException;
import kr.or.futur.futurcertification.repository.UserRepository;
import kr.or.futur.futurcertification.service.RedisService;
import kr.or.futur.futurcertification.service.SignService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    private final static Logger log = LoggerFactory.getLogger(CertificationControllerTest.class);

    @BeforeEach
    void setup() {
        /* 파라미터 세팅 */
        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
        signUpRequestDTO.setId("test");
        signUpRequestDTO.setPassword("test");
        signUpRequestDTO.setName("테스트");
        signUpRequestDTO.setRole("ROLE_USER");
        signUpRequestDTO.setEmail("kjsung0129@gmail.com");
        signUpRequestDTO.setPhoneNumber(phoneNumber);
        signUpRequestDTO.setBirthDay("1999-01-01");

        /* Redis 정보 비우기 */
        redisService.deleteData(CertificationCodeType.REGISTER.name() + "_" + signUpRequestDTO.getPhoneNumber());
        redisService.deleteData(CertificationCodeType.REGISTER.name() + "_result_" + signUpRequestDTO.getPhoneNumber());

        /* 인증번호 번호 저장 및 확인 */
        redisService.setDataExpire(CertificationCodeType.REGISTER.name() +"_" + signUpRequestDTO.getPhoneNumber(), String.valueOf(Math.round(Math.random() * 10000)), 10);
        redisService.setDataExpire(CertificationCodeType.REGISTER.name() +"_result_" + signUpRequestDTO.getPhoneNumber(), "true", 10);

        /* 회원 가입 */
        SignUpResultDTO signUpResultDTO = signService.signUp(signUpRequestDTO);

        log.debug("setUp 결과 : {}", signUpResultDTO);
    }

    @AfterEach
    void cleanUp() {
        /* 생성한 사용자 삭제 */
        User user = userRepository.findByUserId("test")
                .orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);

        /* 레디스 정보 비우기 */
        redisService.deleteData(CertificationCodeType.REGISTER.name() + "_" + phoneNumber);
        redisService.deleteData(CertificationCodeType.REGISTER.name() + "_result_" + phoneNumber);
    }

    @Test
    @Order(4)
    @DisplayName("유레카 서버 연결 확인")
    void connected() throws Exception {
        Map<String, String> input = new HashMap<>();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/certification/connected")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("connected-test",
//                        requestFields(),
                        responseFields(
                                fieldWithPath("isConnected").description("유레카 서버 연결 여부")
                        )
                ))
                .andExpect(jsonPath("$.isConnected").value(is(true)));
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("회원가입")
    void signUp() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("id", "test" + Math.round(Math.random() * 10000));
        inputs.put("password", "test");
        inputs.put("name", "test");
        inputs.put("role", "ROLE_USER");
        inputs.put("email", "test@test.com");
        inputs.put("phoneNumber", phoneNumber);
        inputs.put("address", "test");
        inputs.put("birthDay", "2022-01-01");

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
                                fieldWithPath("msg").description("메세지")
                        )
                ))
                .andExpect(jsonPath("$.success").value(is(true)))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.msg").value(is("회원가입을 성공했습니다.")));
    }

    @Test
    @Order(5)
    @Transactional
    @DisplayName("로그인")
    void signIn() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("userId", "test");
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
                                fieldWithPath("token").description("토큰(1일)"),
                                fieldWithPath("refreshToken").description("리프레시 토큰(30일)"),
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("code").description("상태여부"),
                                fieldWithPath("msg").description("메세지")
                        )
                ))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.success").value(is(true)))
                .andExpect(jsonPath("$.code").value(is(200)))
                .andExpect(jsonPath("$.msg").value(is("로그인을 성공했습니다.")));
    }

    @Test
    @Order(1)
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
    @Order(2)
    @DisplayName("인증번호 확인")
    void confirmCertificationNumber() throws Exception {
        Map<String, Object> inputs = new HashMap<>();
        String certificationNumber = redisService.getData(CertificationCodeType.REGISTER.name() + "_" + phoneNumber);

        inputs.put("phoneNumber", phoneNumber);
        inputs.put("type", CertificationCodeType.REGISTER.name());
        inputs.put("certificationNumber", certificationNumber);

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
                                fieldWithPath("msg").description("응답 메세지"),
                                fieldWithPath("equal").description("인증번호 확인 여부")
                        )
                ))
                .andExpect(jsonPath("$.equal").value(is(true)))
                .andExpect(jsonPath("$.code").value(is(200)));
    }

//    @Test
    @Order(6)
    @Transactional
    @DisplayName("사용자 삭제")
    void deleteUser() throws Exception {
        Map<String, Object> inputs = new HashMap<>();
        String certificationCode = String.valueOf((int) (Math.random() * 90000) + 10000);

        /* 파라미터 세팅 */
        UserDTO userDTO = signService.findAllByDelYn(PageRequest.of(0, 100), false)
                .stream().findFirst().orElseThrow(UserNotFoundException::new);

        String userId = userDTO.getUserId();

        inputs.put("certificationNumber", certificationCode);
        inputs.put("type", "DELETE");
        inputs.put("phoneNumber", userDTO.getPhoneNumber());

        /* 임으의 인증번호 저장 */
        redisService.setDataExpire(CertificationCodeType.DELETE.name() + "_" + phoneNumber, certificationCode, 60);

        /* 인증번호 확인 */
        mockMvc.perform(MockMvcRequestBuilders.put("/certification/confirm-certification-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.equal").value(is(true)));

        /* 사용자 삭제 */
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/certification/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-user-test",
                        pathParameters(
                                parameterWithName("userId").description("사용자 ID")
                        )
                ));
    }

    @Test
    @Order(7)
    @Transactional
    @DisplayName("삭제된 회원 복구")
    void restoreUser() throws Exception {
        /* 삭제된 사용자 찾기 */
        List<UserDTO> userDTOs = signService.findAllByDelYn(PageRequest.of(0, 100), true);

        /* 조회된 여부 없을 경우 에러 발생 */
        if(userDTOs.isEmpty()) {
            throw new UserNotFoundException("조회된 삭제 사용자가 없습니다.");
        }

        String userId = userDTOs.get(0).getUserId();

        /* 특정 사용자 찾기 */
        UserDTO userDTO = signService.findUserId(userId);

        /* 임의의 인증번호 생성, 저장 및 확인 */
        String certificationCode = String.valueOf((int) (Math.random() * 90000) + 10000);
        redisService.setDataExpire(CertificationCodeType.RESTORE.name() + "_" + userDTO.getPhoneNumber(), certificationCode, 60);
        redisService.setDataExpire(CertificationCodeType.RESTORE.name() + "_result_" + userDTO.getPhoneNumber(), String.valueOf(true),60);

        /* 사용자 복구 */
        mockMvc.perform(RestDocumentationRequestBuilders.put("/certification/restore/{userId}", userDTO.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("restore-user-test",
                        pathParameters(
                                parameterWithName("userId").description("사용자 ID")
                        )
                ));
    }

    @Test
    @DisplayName("사용자 조회")
    void findUserIdAndPhoneNumber() throws Exception {
        String userIdAndPhoneNumber = "test";

        mockMvc.perform(RestDocumentationRequestBuilders.get("/certification/{userIdAndPhoneNumber}", userIdAndPhoneNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-user-id-and-phone-number-test",
                        pathParameters(
                                parameterWithName("userIdAndPhoneNumber").description("사용자 ID 또는 휴대전화 번호")
                        )
                ));
    }
}