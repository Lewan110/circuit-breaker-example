package com.adrian.circuit.breaker;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class CircuitBreakerITest {

    private final static String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";
    private final static String CONTENT_TYPE_HEADER = "Content-Type";

    @Autowired
    MockMvc mockMvc;

    @Value("${api.users.path}")
    private String usersPath;

    @Value("${circuit-breaker.timeout-duration-in-seconds}")
    private Integer circuitBreakerTimeout;

    @Before
    public void init() {
        listAllStubMappings().getMappings().forEach(WireMock::removeStub);
    }

    @After
    public void clean() {
        listAllStubMappings().getMappings().forEach(WireMock::removeStub);
    }

    @Test
    public void returnsUsersList() throws Exception {

        //given
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/"+ usersPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(exampleUsersResponse())
                        .withHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)));

        //when
        ResultActions response = mockMvc.perform(get("/users")
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE))
                .andDo(print());

        //then
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$[0].name").value("Leanne Graham"));
    }

    @Test
    public void returnsDefaultUsersList() throws Exception {

        //given
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/"+ usersPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay((int) Duration.ofSeconds(circuitBreakerTimeout + 1).toMillis())
                        .withBody(exampleUsersResponse())
                        .withHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)));

        //when
        ResultActions response = mockMvc.perform(get("/users")
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE))
                .andDo(print());

        //then
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$[0].name").value("example"));
    }


    private String exampleUsersResponse() {
        return "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Leanne Graham\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"Ervin Howell\"\n" +
                "  }\n" +
                "]";
    }
}
