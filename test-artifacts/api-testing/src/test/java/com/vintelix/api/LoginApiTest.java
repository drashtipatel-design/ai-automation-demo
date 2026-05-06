package com.vintelix.api;

import com.vintelix.base.BaseApiTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * RestAssured API tests for SCRUM-7: POST /api/auth/login and POST /api/auth/signup
 */
public class LoginApiTest extends BaseApiTest {

    // ─────────────── LOGIN POSITIVE TESTS ───────────────

    // TC-035
    @Test(description = "TC-035: POST /api/auth/login - valid username returns 200 with token")
    public void testLoginWithValidUsername() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", VALID_USERNAME);
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("token", notNullValue())
            .body("token", not(emptyString()))
            .time(lessThan(2000L));
    }

    // TC-036
    @Test(description = "TC-036: POST /api/auth/login - valid email returns 200 with token")
    public void testLoginWithValidEmail() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", VALID_EMAIL);
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(200)
            .body("token", notNullValue());
    }

    // TC-043
    @Test(description = "TC-043: Token in response is valid JWT format (3 segments)")
    public void testTokenIsValidJwt() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", VALID_USERNAME);
        body.put("password", VALID_PASSWORD);

        String token = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(200)
            .extract()
            .path("token");

        Assert.assertNotNull(token, "Token should not be null");
        String[] parts = token.split("\\.");
        Assert.assertEquals(parts.length, 3,
                "JWT must consist of 3 base64url-encoded segments separated by dots");
        for (String part : parts) {
            Assert.assertFalse(part.isEmpty(), "Each JWT segment must be non-empty");
        }
    }

    // TC-044
    @Test(description = "TC-044: Response schema contains required fields")
    public void testResponseSchema() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", VALID_USERNAME);
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(200)
            .header("Content-Type", containsString("application/json"))
            .body("token", notNullValue());
    }

    // ─────────────── LOGIN NEGATIVE TESTS ───────────────

    // TC-037
    @Test(description = "TC-037: POST /api/auth/login - wrong password returns 401")
    public void testLoginWithWrongPassword() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", VALID_USERNAME);
        body.put("password", "WrongPassword999");

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(401)
            .extract()
            .response();

        String responseBody = response.asString();
        Assert.assertFalse(responseBody.toLowerCase().contains("password is incorrect"),
                "Error must not reveal which field is wrong");
        Assert.assertFalse(responseBody.contains("token"),
                "No token should be returned on failed login");
    }

    // TC-038
    @Test(description = "TC-038: POST /api/auth/login - non-existent user returns 401 (same as wrong password)")
    public void testLoginWithNonExistentUser() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", "ghost_user_xyz_nonexistent");
        body.put("password", VALID_PASSWORD);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(401)
            .extract()
            .response();

        String responseBody = response.asString();
        Assert.assertFalse(responseBody.toLowerCase().contains("user not found"),
                "Error must not reveal that user does not exist (prevents user enumeration)");
        Assert.assertFalse(responseBody.toLowerCase().contains("does not exist"),
                "Error must not reveal that user does not exist");
    }

    // TC-039
    @Test(description = "TC-039: POST /api/auth/login - missing usernameOrEmail returns 400")
    public void testLoginMissingUsername() {
        Map<String, String> body = new HashMap<>();
        body.put("password", VALID_PASSWORD);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(400);
    }

    // TC-040
    @Test(description = "TC-040: POST /api/auth/login - missing password returns 400")
    public void testLoginMissingPassword() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", VALID_USERNAME);

        given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(400);
    }

    // TC-041
    @Test(description = "TC-041: POST /api/auth/login - empty body returns 400")
    public void testLoginEmptyBody() {
        given()
            .spec(requestSpec)
            .body("{}")
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .statusCode(400);
    }

    // ─────────────── RATE LIMITING TESTS ───────────────

    // TC-042
    @Test(description = "TC-042: POST /api/auth/login - rate limiting after N failed attempts")
    public void testRateLimiting() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", VALID_USERNAME);
        body.put("password", "WrongPass");

        int rateLimitThreshold = 5;
        int lastStatus = 401;

        for (int i = 0; i <= rateLimitThreshold; i++) {
            Response response = given()
                .spec(requestSpec)
                .body(body)
            .when()
                .post(LOGIN_ENDPOINT)
            .then()
                .extract()
                .response();
            lastStatus = response.getStatusCode();
        }

        Assert.assertEquals(lastStatus, 429,
                "After " + rateLimitThreshold + " failed attempts, endpoint should return HTTP 429");
    }

    // ─────────────── EDGE CASES ───────────────

    // TC-031 (API portion)
    @Test(description = "TC-031: Very long username - handled gracefully (no 500)")
    public void testVeryLongUsername() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", "a".repeat(1000));
        body.put("password", VALID_PASSWORD);

        int status = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .extract()
            .statusCode();

        Assert.assertTrue(status == 400 || status == 401,
                "Long username should return 400 or 401, not a server error (500)");
    }

    // TC-029 (API portion)
    @Test(description = "TC-029: SQL injection in usernameOrEmail - rejected cleanly")
    public void testSqlInjectionRejected() {
        Map<String, String> body = new HashMap<>();
        body.put("usernameOrEmail", "' OR '1'='1");
        body.put("password", "anything");

        int status = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(LOGIN_ENDPOINT)
        .then()
            .extract()
            .statusCode();

        Assert.assertNotEquals(status, 200,
                "SQL injection attempt should not return 200");
        Assert.assertNotEquals(status, 500,
                "SQL injection should not cause server error - must be parameterized queries");
    }
}
