package com.vintelix.api;

import com.vintelix.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * RestAssured API tests for POST /api/auth/signup (SCRUM-7 extension)
 */
public class SignupApiTest extends BaseApiTest {

    private String uniqueEmail() {
        return "user_" + System.currentTimeMillis() + "@test.com";
    }

    private String uniqueUsername() {
        return "user_" + System.currentTimeMillis();
    }

    // TC-045
    @Test(description = "TC-045: POST /api/auth/signup - valid data returns 200/201 with token")
    public void testSignupWithValidData() {
        Map<String, String> body = new HashMap<>();
        body.put("username", uniqueUsername());
        body.put("email", uniqueEmail());
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(SIGNUP_ENDPOINT)
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(201)))
            .body("token", notNullValue());
    }

    // TC-046
    @Test(description = "TC-046: POST /api/auth/signup - duplicate email returns 409")
    public void testSignupWithDuplicateEmail() {
        Map<String, String> body = new HashMap<>();
        body.put("username", "existinguser");
        body.put("email", VALID_EMAIL);
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(SIGNUP_ENDPOINT)
        .then()
            .statusCode(409)
            .body("token", nullValue());
    }

    // TC-047
    @Test(description = "TC-047: POST /api/auth/signup - missing username returns 400")
    public void testSignupMissingUsername() {
        Map<String, String> body = new HashMap<>();
        body.put("email", uniqueEmail());
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(SIGNUP_ENDPOINT)
        .then()
            .statusCode(400);
    }

    @Test(description = "Signup - missing email returns 400")
    public void testSignupMissingEmail() {
        Map<String, String> body = new HashMap<>();
        body.put("username", uniqueUsername());
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(SIGNUP_ENDPOINT)
        .then()
            .statusCode(400);
    }

    @Test(description = "Signup - invalid email format returns 400")
    public void testSignupInvalidEmailFormat() {
        Map<String, String> body = new HashMap<>();
        body.put("username", uniqueUsername());
        body.put("email", "notanemail");
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(SIGNUP_ENDPOINT)
        .then()
            .statusCode(400);
    }

    @Test(description = "Signup - empty body returns 400")
    public void testSignupEmptyBody() {
        given()
            .spec(requestSpec)
            .body("{}")
        .when()
            .post(SIGNUP_ENDPOINT)
        .then()
            .statusCode(400);
    }

    @Test(description = "Signup - token is JWT after successful registration")
    public void testSignupTokenIsJwt() {
        Map<String, String> body = new HashMap<>();
        body.put("username", uniqueUsername());
        body.put("email", uniqueEmail());
        body.put("password", VALID_PASSWORD);

        String token = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(SIGNUP_ENDPOINT)
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(201)))
            .extract()
            .path("token");

        Assert.assertNotNull(token);
        Assert.assertEquals(token.split("\\.").length, 3,
                "Signup token must be valid JWT");
    }
}
