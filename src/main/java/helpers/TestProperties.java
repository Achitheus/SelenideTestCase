package helpers;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"system:properties",
        "system:env",
        "file:src/test/resources/test.properties"})
public interface TestProperties extends Config {
    @Key("user.data.dir")
    String chromeDir();

    @Key("profile.dir")
    String profileDir();

    @Key("use.profile")
    boolean useChromeProfile();


}
