package ru.bellintegrator;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.SoftAssertsExtension;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.TestProperties;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.open;
import static helpers.Properties.testProperties;

@ExtendWith({SoftAssertsExtension.class})
public class BaseTest {
    public static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    /**
     * Настройка тестов и окружения в зависимости от Maven профиля. Тесты запускаются из-под профиля хрома,
     * если соответствующее значение проперти {@link TestProperties#useBrowserProfile()}
     * установлено в {@code true}. То же относится и к использованию {@code Selenoid} и режима {@code headless}
     *
     * @author Юрий Юрченко
     */
    @BeforeAll
    public static void setup() {
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().includeSelenideSteps(false));
        log.info("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   NEW TESTS RUN   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.info("Active maven profile: {}", testProperties.mavenProfile());
        ChromeOptions options = new ChromeOptions();
        Configuration.browserCapabilities = options;
        Configuration.browser = "chrome";
        Configuration.timeout = 6_000;
        Configuration.browserSize = "1920x1080";
        Configuration.headless = testProperties.headless();
        Configuration.remote = testProperties.useSelenoid() ? "http://localhost:4444/wd/hub" : null;
        options.addArguments("--disable-extensions");
        if (testProperties.headless()) {
            options.addArguments("--user-agent=" + editedUserAgent());
        }
        if (testProperties.useBrowserProfile()) {
            options.addArguments("--user-data-dir=" + testProperties.chromeDir());
            options.addArguments("--profile-directory=" + testProperties.profileDir());
        }
    }

    @BeforeEach
    public void options(TestInfo testInfo) {
        log.info(" <<<<<<<<<  " + testInfo.getDisplayName() + "  is running >>>>>>>>>");
    }

    public static String editedUserAgent() {
        open("http://github.com");
        String currentUserAgent = Selenide.getUserAgent();
        log.info("User agent supposed to change is: {}", currentUserAgent);
        String editedUserAgent = currentUserAgent.replaceAll("(Headless)", "");
        log.info("User-Agent value can be used: {}", editedUserAgent);
        Selenide.closeWebDriver();
        return editedUserAgent;
    }

    @AfterEach
    public void afterEach() {
        Selenide.closeWebDriver();
    }

}
