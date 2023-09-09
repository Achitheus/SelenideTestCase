package ru.bellintegrator;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseTest {
    public static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeAll
    public static void init() {
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   New tests run   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Configuration.browser = "chrome";
        Configuration.timeout = 10_000;
        Configuration.holdBrowserOpen = true;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=" + System.getenv("AUTOMATION_PROJECTS")
                                                + "\\SelenideTestCase\\chrome-profiles");
        options.addArguments("--profile-directory=Profile 1");
        options.addArguments("--disable-extensions");
        Configuration.browserCapabilities = options;
    }
}
