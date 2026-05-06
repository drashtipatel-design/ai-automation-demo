package com.vintelix.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

public class BaseApiTest {

    protected static RequestSpecification requestSpec;
    protected static final String BASE_URI = "http://localhost:8080";
    protected static final String LOGIN_ENDPOINT = "/api/auth/login";
    protected static final String SIGNUP_ENDPOINT = "/api/auth/signup";

    protected static final String VALID_USERNAME = "drashti@example.com";
    protected static final String VALID_EMAIL    = "drashti@example.com";
    protected static final String VALID_PASSWORD = "Root@1234";

    @BeforeSuite
    public void globalSetup() {
        RestAssured.baseURI = BASE_URI;
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    protected static String obtainValidToken() {
        return RestAssured
                .given()
                .spec(requestSpec)
                .body("{\"usernameOrEmail\":\"" + VALID_USERNAME + "\",\"password\":\"" + VALID_PASSWORD + "\"}")
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
