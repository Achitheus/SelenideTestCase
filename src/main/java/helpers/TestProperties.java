package helpers;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties",
        "system:env",
        "file:src/test/resources/test.properties"})
public interface TestProperties extends Config {
    @Key("chrome.dir")
    String chromeDir();

    @Key("chrome.profile.dir")
    String profileDir();

    @Key("chrome.profile.use")
    boolean useChromeProfile();


}
