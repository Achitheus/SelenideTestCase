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

import static helpers.Properties.testProperties;

@ExtendWith({SoftAssertsExtension.class})
public class BaseTest {
    public static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeAll
    public static void setup() {
        log.info("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().includeSelenideSteps(false));
    }

    @BeforeEach
    public void options(TestInfo testInfo) {
        log.info(" <<<<<<<<<  " + testInfo.getDisplayName() + "  is running >>>>>>>>>");
        ChromeOptions options = new ChromeOptions();
        Configuration.browserCapabilities = options;
        Configuration.browser = "chrome";
        Configuration.timeout = 6_000;
        Configuration.browserSize = "1920x1080";
        options.addArguments("--disable-extensions");
        if (!testProperties.useChromeProfile()) {
            return;
        }
        options.addArguments("--user-data-dir=" + testProperties.chromeDir());
        options.addArguments("--profile-directory=" + testProperties.profileDir());
    }

    @AfterEach
    public void ending() {
        Selenide.closeWindow();
    }
}
