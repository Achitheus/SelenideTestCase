package helpers;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Stopwatch;

import java.time.Duration;

public class SelenideCustom {
    /**
     * Формирует лямбду {@link Command}, сообщающую выполнилось ли условие {@code condition}
     * в течение времени {@code timeout}.
     * Работает так же как и {@link SelenideElement#is(Condition)} с той лишь разницей, что умеет ждать. Используется в
     * качестве параметра для метода {@link SelenideElement#execute(Command)}.<p>
     * Пример использования:<pre>
     *     if (button.execute(metCondition(appear, Duration.ofSeconds(2)))) {...}</pre>
     *
     * @param condition Проверяемое условие.
     * @param timeout   Предполагаемое время, за которое условие может выполниться.
     * @return Лямбду, возвращающую {@code true}, если условие {@code condition} наступило за
     * время {@code timeout}, иначе - {@code false}.
     */
    public static Command<Boolean> metCondition(Condition condition, Duration timeout) {
        Stopwatch stopwatch = new Stopwatch(timeout.toMillis());
        return (proxy, locator, args) -> {
            do {
                if (proxy.is(condition)) {
                    return true;
                }
                stopwatch.sleep(200);
            } while (!stopwatch.isTimeoutReached());
            return false;
        };
    }

}
