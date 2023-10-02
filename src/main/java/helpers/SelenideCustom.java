package helpers;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.UIAssertionError;

import java.util.function.Function;

public class SelenideCustom {

    public static Command<Boolean> metCondition(Function<SelenideElement, SelenideElement> function) {
        return (proxy, locator, args) -> {
            try {
                function.apply(proxy);
                return true;
            } catch (UIAssertionError th) {
                return false;
            }
        };
    }

}
