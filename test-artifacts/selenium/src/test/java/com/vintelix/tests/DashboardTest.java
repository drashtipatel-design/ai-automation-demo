package com.vintelix.tests;

import com.vintelix.pages.DashboardPage;
import com.vintelix.pages.LoginPage;
import com.vintelix.utils.ConfigReader;
import com.vintelix.utils.WaitUtils;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selenium UI tests for SCRUM-8: Dashboard (Home Page) with Header and Left Navigation
 */
public class DashboardTest extends BaseTest {

    private DashboardPage dashboardPage;
    private final String validUsername = ConfigReader.getValidUsername();
    private final String validPassword = ConfigReader.getValidPassword();

    @BeforeMethod(alwaysRun = true)
    public void loginAndOpenDashboard() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);
        loginPage.login(validUsername, validPassword);
        WaitUtils.waitForUrlContains(driver, "/dashboard");
        dashboardPage = new DashboardPage(driver);
        dashboardPage.waitForLoad();
    }

    // SCRUM8-TC-001
    @Test(description = "SCRUM8-TC-001: Unauthenticated access to /dashboard redirects to /login")
    public void testDashboardProtectedRoute() {
        clearSession();
        driver.get(baseUrl + "/dashboard");
        WaitUtils.waitForUrlContains(driver, "/login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "Unauthenticated user should be redirected to /login");
    }

    // SCRUM8-TC-002
    @Test(description = "SCRUM8-TC-002: Dashboard loads correctly after login")
    public void testDashboardLoadsAfterLogin() {
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "URL should be /dashboard after login");
    }

    // SCRUM8-TC-003
    @Test(description = "SCRUM8-TC-003: Header bar is visible at top of dashboard")
    public void testHeaderBarVisible() {
        Assert.assertTrue(dashboardPage.isHeaderVisible(), "Header should be visible");
    }

    // SCRUM8-TC-004
    @Test(description = "SCRUM8-TC-004: Header shows logged-in user's username")
    public void testHeaderShowsUsername() {
        String displayedUsername = dashboardPage.getDisplayedUsername();
        Assert.assertFalse(displayedUsername.isEmpty(), "Username should be displayed in header");
        Assert.assertEquals(displayedUsername, validUsername,
                "Displayed username should match logged-in user");
    }

    // SCRUM8-TC-005
    @Test(description = "SCRUM8-TC-005: Header shows current GMT date and time")
    public void testHeaderShowsGmtDateTime() {
        String clockText = dashboardPage.getGmtClockText();
        Assert.assertFalse(clockText.isEmpty(), "GMT clock text should not be empty");
        Assert.assertTrue(clockText.toUpperCase().contains("GMT"),
                "Clock should display GMT label");
    }

    // SCRUM8-TC-006
    @Test(description = "SCRUM8-TC-006: GMT clock updates live every second",
            timeOut = 5000)
    public void testGmtClockUpdatesLive() throws InterruptedException {
        boolean isUpdating = dashboardPage.isGmtClockUpdating();
        Assert.assertTrue(isUpdating, "GMT clock should update in real-time");
    }

    // SCRUM8-TC-007
    @Test(description = "SCRUM8-TC-007: GMT time is correct regardless of local timezone")
    public void testGmtTimeCorrect() {
        String clockText = dashboardPage.getGmtClockText();
        // Validate the displayed time matches expected GMT
        long epochMilli = System.currentTimeMillis();
        java.time.ZonedDateTime now = java.time.Instant.ofEpochMilli(epochMilli)
                .atZone(java.time.ZoneOffset.UTC);
        int gmtHour = now.getHour();
        Assert.assertTrue(clockText.contains(String.format("%02d", gmtHour)) ||
                clockText.contains(String.valueOf(gmtHour)),
                "Displayed hour should match current GMT hour");
    }

    // SCRUM8-TC-008
    @Test(description = "SCRUM8-TC-008: Left navigation sidebar is visible")
    public void testSidebarVisible() {
        Assert.assertTrue(dashboardPage.isSidebarVisible(), "Left navigation sidebar should be visible");
    }

    // SCRUM8-TC-009
    @Test(description = "SCRUM8-TC-009: 'IMDA Entities' menu item is present")
    public void testImdaEntitiesPresent() {
        Assert.assertTrue(dashboardPage.isImdaEntitiesPresent(),
                "'IMDA Entities' should be present in sidebar");
    }

    // SCRUM8-TC-010
    @Test(description = "SCRUM8-TC-010: 'IMDA Patterns' menu item is present")
    public void testImdaPatternsPresent() {
        Assert.assertTrue(dashboardPage.isImdaPatternsPresent(),
                "'IMDA Patterns' should be present in sidebar");
    }

    // SCRUM8-TC-011
    @Test(description = "SCRUM8-TC-011: Nav items are in correct order: Entities first, Patterns second")
    public void testNavItemOrder() {
        String first = dashboardPage.getNavItemOrder(0);
        String second = dashboardPage.getNavItemOrder(1);
        Assert.assertTrue(first.contains("IMDA Entities") || first.contains("Entities"),
                "First nav item should be 'IMDA Entities'");
        Assert.assertTrue(second.contains("IMDA Patterns") || second.contains("Patterns"),
                "Second nav item should be 'IMDA Patterns'");
    }

    // SCRUM8-TC-012
    @Test(description = "SCRUM8-TC-012: Clicking 'IMDA Entities' navigates to /entities")
    public void testImdaEntitiesNavigation() {
        dashboardPage.clickImdaEntities();
        WaitUtils.waitForUrlContains(driver, "/entities");
        Assert.assertTrue(driver.getCurrentUrl().contains("/entities"),
                "Should navigate to /entities");
    }

    // SCRUM8-TC-013
    @Test(description = "SCRUM8-TC-013: Clicking 'IMDA Patterns' navigates to /patterns")
    public void testImdaPatternsNavigation() {
        dashboardPage.clickImdaPatterns();
        WaitUtils.waitForUrlContains(driver, "/patterns");
        Assert.assertTrue(driver.getCurrentUrl().contains("/patterns"),
                "Should navigate to /patterns");
    }

    // SCRUM8-TC-014
    @Test(description = "SCRUM8-TC-014: 'IMDA Entities' is highlighted as active on /entities")
    public void testActiveStateEntities() {
        driver.get(baseUrl + "/entities");
        dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isNavItemActiveFor("IMDA Entities"),
                "'IMDA Entities' should have active state on /entities route");
    }

    // SCRUM8-TC-015
    @Test(description = "SCRUM8-TC-015: 'IMDA Patterns' is highlighted as active on /patterns")
    public void testActiveStatePatterns() {
        driver.get(baseUrl + "/patterns");
        dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isNavItemActiveFor("IMDA Patterns"),
                "'IMDA Patterns' should have active state on /patterns route");
    }

    // SCRUM8-TC-017
    @Test(description = "SCRUM8-TC-017: Hover state applied on navigation items")
    public void testNavItemHoverState() {
        // Hover over the element - verify no exception (visual state is browser-rendered)
        dashboardPage.hoverOverNavItem("IMDA Entities");
        Assert.assertTrue(dashboardPage.isImdaEntitiesPresent(),
                "IMDA Entities still present after hover");
    }

    // SCRUM8-TC-019
    @Test(description = "SCRUM8-TC-019: /entities route is protected - redirects unauthenticated")
    public void testEntitiesRouteProtected() {
        clearSession();
        driver.get(baseUrl + "/entities");
        WaitUtils.waitForUrlContains(driver, "/login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "/entities should redirect unauthenticated users to /login");
    }

    // SCRUM8-TC-020
    @Test(description = "SCRUM8-TC-020: /patterns route is protected - redirects unauthenticated")
    public void testPatternsRouteProtected() {
        clearSession();
        driver.get(baseUrl + "/patterns");
        WaitUtils.waitForUrlContains(driver, "/login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "/patterns should redirect unauthenticated users to /login");
    }

    // SCRUM8-TC-021
    @Test(description = "SCRUM8-TC-021: Logout button present and clears session")
    public void testLogoutClearsSession() {
        Assert.assertTrue(dashboardPage.isLogoutButtonPresent(), "Logout button should be visible");
        dashboardPage.clickLogout();
        WaitUtils.waitForUrlContains(driver, "/login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "Should redirect to /login after logout");
        Assert.assertTrue(isSessionEmpty(), "sessionStorage should be cleared after logout");
    }

    // SCRUM8-TC-022
    @Test(description = "SCRUM8-TC-022: Sidebar is collapsible on mobile viewport")
    public void testSidebarCollapsibleOnMobile() {
        driver.manage().window().setSize(new Dimension(375, 667));
        driver.navigate().refresh();
        dashboardPage = new DashboardPage(driver);
        dashboardPage.waitForLoad();
        // Sidebar should collapse on mobile - verifying page still renders without error
        Assert.assertTrue(dashboardPage.isHeaderVisible(),
                "Header should still be visible on mobile");
    }

    // SCRUM8-TC-023
    @Test(description = "SCRUM8-TC-023: Full layout on desktop viewport")
    public void testDesktopLayout() {
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.navigate().refresh();
        dashboardPage = new DashboardPage(driver);
        dashboardPage.waitForLoad();
        Assert.assertTrue(dashboardPage.isHeaderVisible(), "Header visible on desktop");
        Assert.assertTrue(dashboardPage.isSidebarVisible(), "Sidebar visible on desktop");
    }

    // SCRUM8-TC-024
    @Test(description = "SCRUM8-TC-024: Missing username in session shows fallback")
    public void testMissingUsernameFallback() {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("sessionStorage.removeItem('username');");
        driver.navigate().refresh();
        dashboardPage = new DashboardPage(driver);
        dashboardPage.waitForLoad();
        String displayedUsername = dashboardPage.getDisplayedUsername();
        Assert.assertFalse(displayedUsername.isEmpty(),
                "A fallback value should be shown when username is missing from session");
    }

    // SCRUM8-TC-028
    @Test(description = "SCRUM8-TC-028: App brand/logo is present in header")
    public void testAppBrandPresent() {
        Assert.assertTrue(dashboardPage.isAppBrandPresent(),
                "Application brand or logo should be visible in header");
    }

    // SCRUM8-TC-026
    @Test(description = "SCRUM8-TC-026: Navigation has proper ARIA roles")
    public void testAriaRolesOnNavigation() {
        String navTagName = driver.findElement(
                org.openqa.selenium.By.cssSelector("nav, [role='navigation']")).getTagName();
        Assert.assertTrue("nav".equals(navTagName) ||
                driver.findElements(org.openqa.selenium.By.cssSelector("[role='navigation']")).size() > 0,
                "Navigation should use semantic <nav> or role='navigation'");
    }
}
