package ru.bellintegrator;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.chrome.ChromeOptions;

public class BaseTest {
    @BeforeAll
    public static void init() {
        Configuration.browser = "chrome";
        Configuration.timeout = 10_000;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=C:\\Users\\Admin\\IdeaProjects\\BellLessonSelenide\\chrome-profiles");
        options.addArguments("--profile-directory=Profile3");
        options.addArguments("--disable-extensions");
        Configuration.browserCapabilities = options;
        Configuration.holdBrowserOpen = true;
    }
}
