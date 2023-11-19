package helpers;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties",
        "system:env",
        "file:target/test-classes/test.properties"})
public interface TestProperties extends Config {
    @Key("user.data.dir")
    String chromeDir();

    @Key("profile.dir")
    String profileDir();

    @Key("use.browser.profile")
    boolean useBrowserProfile();

    @Key("maven.profile")
    String mavenProfile();

    @Key("headless")
    boolean headless();

    @Key("use.selenoid")
    boolean useSelenoid();
}
