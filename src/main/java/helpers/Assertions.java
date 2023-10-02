package helpers;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.BiFunction;

public class Assertions {

    @Step("{message}")
    public static SelenideElement selenideAssertWithStep(SelenideElement element, BiFunction<SelenideElement, String, SelenideElement> condition, String message) {
        return condition.apply(element, message);
    }

}
