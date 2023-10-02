package ru.bellintegrator;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.SoftAssertsExtension;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith({SoftAssertsExtension.class})
public class BaseTest {
    public static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeAll
    public static void setup() {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().includeSelenideSteps(false));
        log.info("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @BeforeEach
    public void options(TestInfo testInfo) {
        log.info(" <<<<<<<<<  " + testInfo.getDisplayName() + "  is running >>>>>>>>>");
        ChromeOptions options = new ChromeOptions();
        // Как вариант можно еще абс. путь задать с помощью System.getProperty("user.dir")
        options.addArguments("--user-data-dir=" + System.getenv("AUTOMATION_PROJECTS")
                + "\\SelenideTestCase\\chrome-profiles");
        options.addArguments("--profile-directory=profileForTests");
        options.addArguments("--disable-extensions");
        Configuration.browserCapabilities = options;
        Configuration.browser = "chrome";
        Configuration.timeout = 6_000;
        Configuration.browserSize = "1920x1080";
    }

    @AfterEach
    public void ending() {
        Selenide.closeWindow();
    }
}
