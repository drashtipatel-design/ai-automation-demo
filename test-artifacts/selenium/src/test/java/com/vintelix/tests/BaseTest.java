package com.vintelix.tests;

import com.vintelix.utils.ConfigReader;
import com.vintelix.utils.DriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeMethod
    public void setUp() {
        DriverManager.initDriver();
        driver = DriverManager.getDriver();
        baseUrl = ConfigReader.getBaseUrl();
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }

    protected void clearSession() {
        ((JavascriptExecutor) driver).executeScript("sessionStorage.clear();");
    }

    protected void setSessionToken(String token) {
        ((JavascriptExecutor) driver).executeScript(
                "sessionStorage.setItem('authToken', arguments[0]);", token);
    }

    protected String getSessionToken() {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return sessionStorage.getItem('authToken');");
    }

    protected boolean isSessionEmpty() {
        String token = getSessionToken();
        return token == null || token.isEmpty();
    }
}
