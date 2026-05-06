package com.vintelix.pages;

import com.vintelix.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class DashboardPage {

    private final WebDriver driver;

    @FindBy(css = "header, .header, [data-testid='header']")
    private WebElement header;

    @FindBy(css = "[data-testid='username-display'], .header-username, header .username")
    private WebElement usernameDisplay;

    @FindBy(css = "[data-testid='gmt-clock'], .gmt-clock, .header-time")
    private WebElement gmtClock;

    @FindBy(css = "nav, aside, .sidebar, [data-testid='sidebar']")
    private WebElement sidebar;

    @FindBy(css = "button.logout-btn, button[data-testid='logout'], button[aria-label='Logout']")
    private WebElement logoutButton;

    @FindBy(css = "[data-testid='app-logo'], .app-brand, header .logo")
    private WebElement appBrand;

    private static final By HEADER_LOCATOR = By.cssSelector("header, .header, [data-testid='header']");
    private static final By USERNAME_LOCATOR = By.cssSelector("[data-testid='username-display'], .header-username");
    private static final By GMT_CLOCK_LOCATOR = By.cssSelector("[data-testid='gmt-clock'], .gmt-clock, .header-time");
    private static final By SIDEBAR_LOCATOR = By.cssSelector("nav, aside, .sidebar");
    private static final By NAV_ITEMS_LOCATOR = By.cssSelector("nav a, .sidebar a, .nav-item");
    private static final By IMDA_ENTITIES_LOCATOR = By.linkText("IMDA Entities");
    private static final By IMDA_PATTERNS_LOCATOR = By.linkText("IMDA Patterns");
    private static final By LOGOUT_LOCATOR = By.cssSelector("button.logout-btn, [data-testid='logout']");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public DashboardPage waitForLoad() {
        WaitUtils.waitForVisible(driver, HEADER_LOCATOR);
        WaitUtils.waitForVisible(driver, SIDEBAR_LOCATOR);
        return this;
    }

    public boolean isHeaderVisible() {
        try {
            return WaitUtils.waitForVisible(driver, HEADER_LOCATOR).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getDisplayedUsername() {
        return WaitUtils.waitForVisible(driver, USERNAME_LOCATOR).getText().trim();
    }

    public String getGmtClockText() {
        return WaitUtils.waitForVisible(driver, GMT_CLOCK_LOCATOR).getText().trim();
    }

    public boolean isGmtClockUpdating() throws InterruptedException {
        String first = getGmtClockText();
        Thread.sleep(2000);
        String second = getGmtClockText();
        return !first.equals(second);
    }

    public boolean isSidebarVisible() {
        try {
            return WaitUtils.waitForVisible(driver, SIDEBAR_LOCATOR).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getNavItems() {
        return driver.findElements(NAV_ITEMS_LOCATOR);
    }

    public boolean isImdaEntitiesPresent() {
        try {
            return driver.findElement(IMDA_ENTITIES_LOCATOR).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isImdaPatternsPresent() {
        try {
            return driver.findElement(IMDA_PATTERNS_LOCATOR).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickImdaEntities() {
        WaitUtils.waitForClickable(driver, IMDA_ENTITIES_LOCATOR).click();
    }

    public void clickImdaPatterns() {
        WaitUtils.waitForClickable(driver, IMDA_PATTERNS_LOCATOR).click();
    }

    public String getActiveNavItemText() {
        try {
            WebElement active = driver.findElement(
                    By.cssSelector(".nav-item.active, .nav-item[aria-current='page'], a.active"));
            return active.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isNavItemActiveFor(String itemText) {
        try {
            WebElement item = driver.findElement(
                    By.xpath("//a[contains(text(),'" + itemText + "')]"));
            String cls = item.getAttribute("class");
            String ariaCurrent = item.getAttribute("aria-current");
            return (cls != null && cls.contains("active")) || "page".equals(ariaCurrent);
        } catch (Exception e) {
            return false;
        }
    }

    public void hoverOverNavItem(String itemText) {
        WebElement item = driver.findElement(
                By.xpath("//a[contains(text(),'" + itemText + "')]"));
        new Actions(driver).moveToElement(item).perform();
    }

    public boolean isLogoutButtonPresent() {
        try {
            return WaitUtils.waitForVisible(driver, LOGOUT_LOCATOR).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLogout() {
        WaitUtils.waitForClickable(driver, LOGOUT_LOCATOR).click();
    }

    public boolean isAppBrandPresent() {
        try {
            return appBrand.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getNavItemOrder(int index) {
        List<WebElement> items = getNavItems();
        if (index < items.size()) {
            return items.get(index).getText().trim();
        }
        return "";
    }
}
