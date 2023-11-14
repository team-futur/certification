package kr.or.futur.futurcertification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CertificationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final static Logger log = LoggerFactory.getLogger(CertificationControllerTest.class);


    @Test
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
    @DisplayName("회원가입")
//    @Transactional
    void signUp() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("id", "test" + Math.round(Math.random() * 10000));
        inputs.put("password", "test");
        inputs.put("name", "test");
        inputs.put("role", "ROLE_USER");
        inputs.put("email", "test@test.com");
        inputs.put("phoneNumber", "010-1234-5678");
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
    @DisplayName("로그인")
    void signIn() {
    }

    @Test
    @DisplayName("인증번호 요청")
    void requestCertificationNumber() throws Exception {
        Map<String, Object> inputs = new HashMap<>();

        inputs.put("phoneNumber", "010-6526-3863");
        inputs.put("type", "REGISTER");

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
    void confirmCertificationNumber() {
    }
}