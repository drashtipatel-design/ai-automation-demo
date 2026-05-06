package com.vintelix.pages;

import com.vintelix.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    private final WebDriver driver;

    // Locators
    @FindBy(css = "input[name='usernameOrEmail'], input[placeholder*='Username'], input[placeholder*='Email']")
    private WebElement usernameEmailField;

    @FindBy(css = "input[name='password'], input[type='password']")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit'], button.login-btn")
    private WebElement loginButton;

    @FindBy(css = "[role='alert'], .error-message, [data-testid='error-message']")
    private WebElement errorMessage;

    @FindBy(css = "button[aria-label*='password'], button.toggle-password, [data-testid='password-toggle']")
    private WebElement passwordToggle;

    @FindBy(css = "a[href*='forgot'], a[href*='forgot-password'], [data-testid='forgot-password-link']")
    private WebElement forgotPasswordLink;

    @FindBy(css = "a[href*='signup'], a[href*='register'], [data-testid='signup-link']")
    private WebElement signupLink;

    @FindBy(css = ".field-error, [data-testid='username-error']")
    private WebElement usernameFieldError;

    @FindBy(css = ".field-error + *, [data-testid='password-error']")
    private WebElement passwordFieldError;

    private static final By LOGIN_BUTTON_LOCATOR = By.cssSelector("button[type='submit'], button.login-btn");
    private static final By ERROR_MESSAGE_LOCATOR = By.cssSelector("[role='alert'], .error-message");
    private static final By USERNAME_FIELD_LOCATOR = By.cssSelector("input[name='usernameOrEmail']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl + "/login");
        WaitUtils.waitForVisible(driver, USERNAME_FIELD_LOCATOR);
        return this;
    }

    public LoginPage enterUsernameOrEmail(String value) {
        usernameEmailField.clear();
        usernameEmailField.sendKeys(value);
        return this;
    }

    public LoginPage enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
        return this;
    }

    public void clickLoginButton() {
        WaitUtils.waitForClickable(driver, LOGIN_BUTTON_LOCATOR).click();
    }

    public void login(String usernameOrEmail, String password) {
        enterUsernameOrEmail(usernameOrEmail);
        enterPassword(password);
        clickLoginButton();
    }

    public String getErrorMessage() {
        return WaitUtils.waitForVisible(driver, ERROR_MESSAGE_LOCATOR).getText();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return WaitUtils.waitForVisible(driver, ERROR_MESSAGE_LOCATOR).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginButtonEnabled() {
        return loginButton.isEnabled();
    }

    public String getLoginButtonText() {
        return loginButton.getText();
    }

    public boolean isPasswordVisible() {
        return passwordField.getAttribute("type").equals("text");
    }

    public LoginPage togglePasswordVisibility() {
        passwordToggle.click();
        return this;
    }

    public boolean isUsernameFieldPresent() {
        try {
            return usernameEmailField.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordFieldPresent() {
        try {
            return passwordField.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginButtonPresent() {
        try {
            return loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isForgotPasswordLinkPresent() {
        try {
            return forgotPasswordLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSignupLinkPresent() {
        try {
            return signupLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickForgotPasswordLink() {
        forgotPasswordLink.click();
    }

    public void clickSignupLink() {
        signupLink.click();
    }

    public String getUsernameFieldError() {
        try {
            return WaitUtils.waitForVisible(driver, By.cssSelector(".field-error")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getPasswordFieldAttribute(String attribute) {
        return passwordField.getAttribute(attribute);
    }

    public String getUsernameAriaLabel() {
        return usernameEmailField.getAttribute("aria-label");
    }

    public String getErrorMessageRole() {
        try {
            return WaitUtils.waitForVisible(driver, ERROR_MESSAGE_LOCATOR)
                    .getAttribute("role");
        } catch (Exception e) {
            return "";
        }
    }
}
