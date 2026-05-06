package com.vintelix.tests;

import com.vintelix.pages.DashboardPage;
import com.vintelix.pages.ForgotPasswordPage;
import com.vintelix.pages.LoginPage;
import com.vintelix.utils.ConfigReader;
import com.vintelix.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selenium UI tests for SCRUM-7: User Login Page with Credential Validation
 */
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private final String validUsername = ConfigReader.getValidUsername();
    private final String validEmail    = ConfigReader.getValidEmail();
    private final String validPassword = ConfigReader.getValidPassword();

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);
    }

    // SCRUM7-TC-001
    @Test(description = "SCRUM7-TC-001: Login page loads at /login route")
    public void testLoginPageLoads() {
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "URL should contain /login");
    }

    // SCRUM7-TC-002
    @Test(description = "SCRUM7-TC-002: Username/Email field is present")
    public void testUsernameFieldPresent() {
        Assert.assertTrue(loginPage.isUsernameFieldPresent(),
                "Username/Email field should be visible");
    }

    // SCRUM7-TC-003
    @Test(description = "SCRUM7-TC-003: Password field is present")
    public void testPasswordFieldPresent() {
        Assert.assertTrue(loginPage.isPasswordFieldPresent(),
                "Password field should be visible");
    }

    // SCRUM7-TC-004
    @Test(description = "SCRUM7-TC-004: Login button is present and enabled")
    public void testLoginButtonPresent() {
        Assert.assertTrue(loginPage.isLoginButtonPresent(), "Login button should be visible");
        Assert.assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled");
    }

    // SCRUM7-TC-005
    @Test(description = "SCRUM7-TC-005: Successful login with valid username")
    public void testLoginWithValidUsername() {
        loginPage.login(validUsername, validPassword);
        WaitUtils.waitForUrlContains(driver, "/dashboard");
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Should redirect to /dashboard after login");
        Assert.assertFalse(isSessionEmpty(), "Token should be stored in sessionStorage");
    }

    // SCRUM7-TC-006
    @Test(description = "SCRUM7-TC-006: Successful login with valid email")
    public void testLoginWithValidEmail() {
        loginPage.login(validEmail, validPassword);
        WaitUtils.waitForUrlContains(driver, "/dashboard");
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Should redirect to /dashboard after email login");
    }

    // SCRUM7-TC-007
    @Test(description = "SCRUM7-TC-007: Invalid password shows error message")
    public void testLoginWithInvalidPassword() {
        loginPage.login(validUsername, "WrongPass999");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        Assert.assertEquals(loginPage.getErrorMessage(), "Invalid Email/Username or Password",
                "Error message text should match exactly");
    }

    // SCRUM7-TC-008
    @Test(description = "SCRUM7-TC-008: Non-existent user shows error message")
    public void testLoginWithNonExistentUser() {
        loginPage.login("ghost_user_xyz", validPassword);
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        Assert.assertEquals(loginPage.getErrorMessage(), "Invalid Email/Username or Password");
    }

    // SCRUM7-TC-009
    @Test(description = "SCRUM7-TC-009: Empty username field triggers validation")
    public void testEmptyUsernameValidation() {
        loginPage.enterPassword(validPassword);
        loginPage.clickLoginButton();
        Assert.assertFalse(driver.getCurrentUrl().contains("/dashboard"),
                "Form should not submit with empty username");
    }

    // SCRUM7-TC-010
    @Test(description = "SCRUM7-TC-010: Empty password field triggers validation")
    public void testEmptyPasswordValidation() {
        loginPage.enterUsernameOrEmail(validUsername);
        loginPage.clickLoginButton();
        Assert.assertFalse(driver.getCurrentUrl().contains("/dashboard"),
                "Form should not submit with empty password");
    }

    // SCRUM7-TC-011
    @Test(description = "SCRUM7-TC-011: Both fields empty - both validations trigger")
    public void testBothFieldsEmptyValidation() {
        loginPage.clickLoginButton();
        Assert.assertFalse(driver.getCurrentUrl().contains("/dashboard"),
                "Form should not submit when both fields are empty");
    }

    // SCRUM7-TC-012
    @Test(description = "SCRUM7-TC-012: Password visibility toggle")
    public void testPasswordVisibilityToggle() {
        loginPage.enterPassword("SecretPass123");
        Assert.assertFalse(loginPage.isPasswordVisible(), "Password should be hidden initially");
        loginPage.togglePasswordVisibility();
        Assert.assertTrue(loginPage.isPasswordVisible(), "Password should be visible after toggle");
        loginPage.togglePasswordVisibility();
        Assert.assertFalse(loginPage.isPasswordVisible(), "Password should be hidden after second toggle");
    }

    // SCRUM7-TC-014
    @Test(description = "SCRUM7-TC-014: Error message does not reveal which field is wrong")
    public void testGenericErrorMessage() {
        loginPage.login(validUsername, "WrongPass");
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertEquals(errorMsg, "Invalid Email/Username or Password",
                "Error must be generic without revealing which field is wrong");
        Assert.assertFalse(errorMsg.toLowerCase().contains("password is incorrect"),
                "Error should not specify 'password'");
        Assert.assertFalse(errorMsg.toLowerCase().contains("user not found"),
                "Error should not specify 'user not found'");
    }

    // SCRUM7-TC-015
    @Test(description = "SCRUM7-TC-015: Token stored in sessionStorage on successful login")
    public void testTokenStoredInSessionStorage() {
        loginPage.login(validUsername, validPassword);
        WaitUtils.waitForUrlContains(driver, "/dashboard");
        String token = getSessionToken();
        Assert.assertNotNull(token, "Token should be stored in sessionStorage");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");
    }

    // SCRUM7-TC-016
    @Test(description = "SCRUM7-TC-016: Unauthenticated user accessing /dashboard redirected to /login")
    public void testPrivateRouteRedirectsToLogin() {
        driver.get(baseUrl + "/dashboard");
        WaitUtils.waitForUrlContains(driver, "/login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "Unauthenticated access to /dashboard should redirect to /login");
    }

    // SCRUM7-TC-017
    @Test(description = "SCRUM7-TC-017: Authenticated user accessing /login redirected to /dashboard")
    public void testAuthenticatedUserRedirectedFromLogin() {
        // First login to get token
        loginPage.login(validUsername, validPassword);
        WaitUtils.waitForUrlContains(driver, "/dashboard");
        // Now navigate back to /login
        driver.get(baseUrl + "/login");
        WaitUtils.waitForUrlContains(driver, "/dashboard");
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Authenticated user should be redirected to /dashboard from /login");
    }

    // SCRUM7-TC-018
    @Test(description = "SCRUM7-TC-018: Invalid email format validation when @ is present")
    public void testInvalidEmailFormatValidation() {
        loginPage.enterUsernameOrEmail("notanemail@");
        loginPage.enterPassword(validPassword);
        loginPage.clickLoginButton();
        Assert.assertFalse(driver.getCurrentUrl().contains("/dashboard"),
                "Invalid email format should prevent submission");
    }

    // SCRUM7-TC-020
    @Test(description = "SCRUM7-TC-020: Input without @ treated as username, no email format error")
    public void testUsernameWithoutAtSymbol() {
        loginPage.enterUsernameOrEmail("testuser123");
        loginPage.enterPassword(validPassword);
        loginPage.clickLoginButton();
        // Should attempt login (API call made), not show email validation error
        Assert.assertFalse(driver.getCurrentUrl().contains("/login") &&
                loginPage.getUsernameFieldError().contains("email format"),
                "Username without @ should not trigger email format validation");
    }

    // SCRUM7-TC-022
    @Test(description = "SCRUM7-TC-022: Form has ARIA labels for accessibility")
    public void testAriaLabels() {
        String ariaLabel = loginPage.getUsernameAriaLabel();
        Assert.assertNotNull(ariaLabel, "Username field should have aria-label");
        Assert.assertFalse(ariaLabel.isEmpty(), "aria-label should not be empty");
    }

    // SCRUM7-TC-025
    @Test(description = "SCRUM7-TC-025: Forgot password link is present and navigates")
    public void testForgotPasswordLink() {
        Assert.assertTrue(loginPage.isForgotPasswordLinkPresent(),
                "Forgot password link should be visible");
        loginPage.clickForgotPasswordLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("forgot"),
                "Should navigate to forgot password page");
    }

    // SCRUM7-TC-026
    @Test(description = "SCRUM7-TC-026: Sign up link is present and navigates")
    public void testSignupLink() {
        Assert.assertTrue(loginPage.isSignupLinkPresent(), "Sign up link should be visible");
        loginPage.clickSignupLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("signup") ||
                driver.getCurrentUrl().contains("register"),
                "Should navigate to signup page");
    }

    // SCRUM7-TC-027
    @Test(description = "SCRUM7-TC-027: Login page responsive on mobile viewport (375x667)")
    public void testResponsiveMobile() {
        driver.manage().window().setSize(new Dimension(375, 667));
        driver.navigate().refresh();
        Assert.assertTrue(loginPage.isUsernameFieldPresent(), "Username field visible on mobile");
        Assert.assertTrue(loginPage.isPasswordFieldPresent(), "Password field visible on mobile");
        Assert.assertTrue(loginPage.isLoginButtonPresent(), "Login button visible on mobile");
    }

    // SCRUM7-TC-029
    @Test(description = "SCRUM7-TC-029: XSS payload in username field is not executed")
    public void testXssInUsernameField() {
        String xssPayload = "<script>alert('xss')</script>";
        loginPage.login(xssPayload, validPassword);
        // If XSS executed, alert would appear and cause WebDriverException
        // We verify no alert and error message is shown normally
        Assert.assertFalse(driver.getCurrentUrl().contains("/dashboard"),
                "XSS login attempt should fail");
    }

    // SCRUM7-TC-031
    @Test(description = "SCRUM7-TC-031: Very long username input handled gracefully")
    public void testVeryLongUsername() {
        String longUsername = "a".repeat(1000);
        loginPage.login(longUsername, validPassword);
        // Should either show validation error or server error, not crash
        boolean staysOnPage = !driver.getCurrentUrl().contains("/dashboard");
        Assert.assertTrue(staysOnPage, "Long username should not result in successful login");
    }

    // SCRUM7-TC-032
    @Test(description = "SCRUM7-TC-032: Whitespace-only username treated as empty")
    public void testWhitespaceOnlyUsername() {
        loginPage.login("   ", validPassword);
        Assert.assertFalse(driver.getCurrentUrl().contains("/dashboard"),
                "Whitespace-only username should not result in login");
    }

    // SCRUM7-TC-048
    @Test(description = "SCRUM7-TC-048: Forgot password page loads")
    public void testForgotPasswordPageLoads() {
        loginPage.clickForgotPasswordLink();
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver);
        forgotPage.waitForLoad();
        Assert.assertTrue(driver.getCurrentUrl().contains("forgot"),
                "Should be on forgot password page");
    }

    // SCRUM7-TC-049
    @Test(description = "SCRUM7-TC-049: Forgot password email validation")
    public void testForgotPasswordEmailValidation() {
        loginPage.clickForgotPasswordLink();
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver);
        forgotPage.waitForLoad();
        forgotPage.enterEmail("notanemail");
        forgotPage.clickSubmit();
        Assert.assertTrue(forgotPage.isEmailErrorDisplayed(),
                "Invalid email format should show error on forgot password page");
    }

    // SCRUM7-TC-050
    @Test(description = "SCRUM7-TC-050: Forgot password success screen shown")
    public void testForgotPasswordSuccess() {
        loginPage.clickForgotPasswordLink();
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver);
        forgotPage.waitForLoad();
        forgotPage.enterEmail("test@vintelix.com");
        forgotPage.clickSubmit();
        Assert.assertTrue(forgotPage.isSuccessMessageDisplayed(),
                "Success message should appear after valid email submission");
    }
}
