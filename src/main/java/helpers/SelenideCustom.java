package helpers;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.UIAssertionError;

import java.util.function.Function;

public class SelenideCustom {
    /**
     * Формирует лямбду {@link Command}, сообщающую прошел ли вызов {@code function} без {@link UIAssertionError}.
     * Работает так же как и, например, {@link SelenideElement#isDisplayed()} с той лишь разницей, что умеет ждать. Используется в
     * качестве параметра для метода {@link SelenideElement#execute(Command)}.<p>
     * Пример использования:<pre>
     *         if(button.execute(metCondition(element -> element.should(appear)))){...}</pre>
     *
     * @param function Метод, способный вызвать {@link UIAssertionError}.
     * @return Лямбду, возвращающую {@code true}, если вызов  {@code function} прошел без {@link UIAssertionError},
     * иначе - {@code false}.
     * @author Юрий Юрченко
     */
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
