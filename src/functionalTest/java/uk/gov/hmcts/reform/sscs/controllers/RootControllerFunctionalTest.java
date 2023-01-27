package uk.gov.hmcts.reform.sscs.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("functional")
class RootControllerFunctionalTest {
    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    @Value("${spring.application.name}")
    private String applicationName;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void functionalTest() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get()
            .then()
            .extract().response();

        assertThat(response.statusCode()).as("Should return okay").isEqualTo(OK.value());
        assertThat(response.asString()).contains("Welcome to");
        assertThat(response.asString()).contains(applicationName);
    }
}
