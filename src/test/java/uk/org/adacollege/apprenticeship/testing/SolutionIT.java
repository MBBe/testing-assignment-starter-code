package uk.org.adacollege.apprenticeship.testing;

import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;

public class SolutionIT {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String startUrl;
    private static String myWhipbirdsMenuId = "my-whipbirds-menu";
    private static String aboutMenuId = "about-menu";
    private static String logOutMenuId = "log-out-menu";
    private static String logInMenuId = "log-in-menu";
    private static String emailInputId = "email";
    private static String passwordInputId = "password";
    private static String validEmail = "manasseh.boyd@adacollege.org.uk";
    private static String invalidEmail = validEmail + ".nothing";
    private static String validPassword = "whipit";
    private static String invalidPassword = validPassword + "-invalid";
    private static String logInButtonId = "login-button";
    private static String logOutButtonId = "log-out-button";
    private static String popupMessageId = "popup-message";

    // ========= UTILITY METHODS =========

    /**
     * Source & usage: https://stackoverflow.com/a/5709805
     */
    private static Function<WebDriver, WebElement> presenceOfElementLocated(final By locator) {
        return new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        };
    }

    private static void logIn(Boolean withValidCredentials) {
        String email = withValidCredentials ? validEmail : invalidEmail;
        String password = withValidCredentials ? validPassword : invalidPassword;

        wait.until(presenceOfElementLocated(By.id(logInMenuId)));
        driver.findElement(By.id(logInMenuId)).click();

        wait.until(presenceOfElementLocated(By.id(emailInputId)));
        driver.findElement(By.id(emailInputId)).sendKeys(email);

        wait.until(presenceOfElementLocated(By.id(passwordInputId)));
        driver.findElement(By.id(passwordInputId)).sendKeys(password);

        wait.until(presenceOfElementLocated(By.id(logInButtonId)));
        driver.findElement(By.id(logInButtonId)).click();

        if (withValidCredentials) {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.getTitle().equals("whipbird: my whipbirds");
                }
            });
        }
    }

    private static void logOut() {
        Boolean isLoggedIn = (driver.findElements(By.id(logOutMenuId)).size() > 0);

        if (isLoggedIn) {
            wait.until(presenceOfElementLocated(By.id(logOutMenuId)));
            driver.findElement(By.id(logOutMenuId)).click();

            wait.until(presenceOfElementLocated(By.id(logOutButtonId)));
            driver.findElement(By.id(logOutButtonId)).click();
        }
    }

    private static void assertElementPresent(String elementId) {
        wait.until(presenceOfElementLocated(By.id(elementId)));
        assertTrue(driver.findElements(By.id(elementId)).size() == 1);
    }

    private static void assertElementNotPresent(String elementId) {
        assertTrue(driver.findElements(By.id(elementId)).size() == 0);
    }

    private static void assertTitleEquals(String expectedTitle) {
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getTitle().equals(expectedTitle);
            }
        });
        assertTrue(result);
    }

    private static void assertUrlEquals(String expectedUrl) {
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getCurrentUrl().equals(expectedUrl);
            }
        });
        assertTrue(result);
    }

    private static void assertElementTextEquals(By selector, String expectedText) {
        // TODO: implement this method
        // - use assertTitleEquals() as an example pattern to follow
        // - but instead of return driver.getTitle().equals(expectedTitle)
        //   call driver.findElement() with the selector provided
        //   and then get the text from that element
        //   and then check that it equals the expected text
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.findElement(selector).getText().equals(expectedText);
            }
        });
        assertTrue(result);
    }

    // ========= SCAFFOLDING =========

    @BeforeClass
    public static void beforeAll() {
        startUrl = "http://whipbird.mattcalthrop.com/";
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @AfterClass
    public static void afterAll() {
        driver.close();
        driver.quit();
    }

    @Before
    public void beforeEach() {
        driver.get(startUrl);
    }

    @After
    public void afterEach() {
        logOut();
    }

    // ========= TESTS =========

    // --------- WHEN NOT LOGGED IN ---------

    // Step 1
    @Test
    public void notLoggedIn_checkMenus() {
        assertElementPresent(logInMenuId);
        assertElementPresent(aboutMenuId);
        assertElementNotPresent(logOutMenuId);
        assertElementNotPresent(myWhipbirdsMenuId);
    }

    // Step 2
    @Test
    public void notLoggedIn_checkCurrentPage() {
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/login");
        assertTitleEquals("whipbird: log in");
        assertElementTextEquals(By.tagName("h4"),"Log in");
        assertElementTextEquals(By.id("footer-right"),"");



    }

    // Step 3
    @Test
    public void notLoggedIn_clickAboutMenu() {
        wait.until(presenceOfElementLocated(By.id(aboutMenuId)));
        WebElement aboutMenu = driver.findElement(By.id(aboutMenuId));
        wait.until(presenceOfElementLocated(By.id(aboutMenuId)));
        aboutMenu.click();
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/about");
        assertTitleEquals("whipbird: about");
        assertElementTextEquals(By.tagName("h4"),"About this app");
    }

    // Step 4
    @Test
    public void notLoggedIn_logInWithIncorrectCredentials() {
        logIn (false);
        assertElementNotPresent(logOutMenuId);
        assertElementNotPresent(myWhipbirdsMenuId);
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/login");
        assertTitleEquals("whipbird: log in");
        assertElementPresent(popupMessageId);
        assertElementTextEquals(By.id("footer-right"),"");


    }

    // --------- WHEN LOGGED IN ---------

    // Step 5
    @Test
    public void loggedIn_checkMenus() {
        logIn(true);
        assertElementPresent(logOutMenuId);
        assertElementPresent(aboutMenuId);
        assertElementPresent(myWhipbirdsMenuId);
        assertElementNotPresent(logInMenuId);

    }

    // Step 6
    @Test
    public void loggedIn_checkCurrentPage() {
        // TODO
        logIn(true);
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/my-whipbirds");
        //wait.until(presenceOfElementLocated(By.id(startUrl)));
        //assertTitleEquals("whipbird: my whipbirds");
        //assertElementTextEquals(By.tagName("h4"),"Current whipbirds for Manasseh Boyd");
        //assertElementTextEquals(By.id("footer-right"),"Manasseh Boyd");


    }

    // Step 7
    @Test
    public void loggedIn_clickLogOutMenu() {
        // TODO
        //wait.until(presenceOfElementLocated(By.id(logOutButtonId)));
        //WebElement logInButton = driver.findElement(By.id(logOutButtonId));
        //wait.until(presenceOfElementLocated(By.id(logOutButtonId)));
        //logOutButton.click();
        //assertUrlEquals("http://whipbird.mattcalthrop.com/#!/logout");
        //assertTitleEquals("whipbird: log out");
        //assertElementTextEquals(By.tagName("h4"),"Log out");
    }

    // Step 8
    @Test
    public void loggedIn_addNewWhipbird() {
        // TODO
    }

    // Step 9
    @Test
    public void loggedIn_addNewWhipbirdThenDeleteIt() {
        // TODO
        //assertElementPresent(popupMessageId);
    }
}
