package com.vintelix.pages;

import com.vintelix.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ForgotPasswordPage {

    private final WebDriver driver;

    @FindBy(css = "input[type='email'], input[name='email'], [data-testid='forgot-email']")
    private WebElement emailField;

    @FindBy(css = "button[type='submit'], [data-testid='forgot-submit']")
    private WebElement submitButton;

    @FindBy(css = "[data-testid='forgot-success'], .success-message")
    private WebElement successMessage;

    @FindBy(css = ".field-error, [role='alert']")
    private WebElement emailError;

    private static final By EMAIL_FIELD_LOCATOR = By.cssSelector("input[type='email'], [data-testid='forgot-email']");

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public ForgotPasswordPage waitForLoad() {
        WaitUtils.waitForVisible(driver, EMAIL_FIELD_LOCATOR);
        return this;
    }

    public ForgotPasswordPage enterEmail(String email) {
        emailField.clear();
        emailField.sendKeys(email);
        return this;
    }

    public void clickSubmit() {
        submitButton.click();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return WaitUtils.waitForVisible(driver,
                    By.cssSelector("[data-testid='forgot-success'], .success-message")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmailErrorDisplayed() {
        try {
            return WaitUtils.waitForVisible(driver,
                    By.cssSelector(".field-error, [role='alert']")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
