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
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsNull.notNullValue;
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
    @DisplayName("Bean 주입 체크")
    void isCheckOfBean() {
        log.info("=====================================================");
        log.info("mockMvc Dependency Injection : {}", mockMvc);
        log.info("objectMapper Dependency Injection : {}", objectMapper);
        log.info("=====================================================");
    }

    @Test
    @DisplayName("유레카 서버 연결 확인")
    void connectedTest() throws Exception {
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
}