package helpers;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.BiFunction;

public class Assertions {
    /**
     * Применяет функцию {@code  condition} к {@code element} и дублирует степом
     * {@code message} в аллюр-отчет. В качестве {@code condition} предполагается передавать
     * методы {@link com.codeborne.selenide.SelenideElement} {@code should()}, {@code shouldBe()} и т.д.,
     * поскольку для них сообщения в отчете появляются только в случае не наступления условия.
     *
     * @param element   {@link com.codeborne.selenide.SelenideElement}, для которого вызывается {@code  condition}.
     * @param condition Проверяющий-ожидающий метод (например {@code should()}) класса {@link com.codeborne.selenide.SelenideElement},
     *                  вызываемый для {@code element}.
     * @param message   Сообщение, отображаемое в отчете.
     * @return Текущий элемент
     * @author Юрий Юрченко
     */
    @Step("{message}")
    public static SelenideElement selenideAssertWithStep(SelenideElement element, BiFunction<SelenideElement, String, SelenideElement> condition, String message) {
        return condition.apply(element, message);
    }

}
