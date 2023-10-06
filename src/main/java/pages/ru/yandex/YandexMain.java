package pages.ru.yandex;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import pages.BasePage;

import static com.codeborne.selenide.Selenide.*;

public class YandexMain extends BasePage {
    /**
     * Переходит в указанный сервис по {@code serviceName} (чувствительно к регистру).
     *
     * @param serviceName Название сервиса, в который следует перейти.
     * @param nextPage    Объект класса {@link Class}, с помощью которого создается возвращаемый данным методом объект.
     * @param <T>         Тип объекта, возвращаемого данным методом.
     * @return Пэйдж обджект типа {@code T}.
     * @author Юрий Юрченко
     */
    @Step("Переход в сервис \"{serviceName}\"")
    public <T> T goToService(String serviceName, Class<T> nextPage) {
        $x("//input[@id='text' and @aria-label='Запрос']").click();
        if (!goToServiceFast(serviceName)) {
            goToServiceViaAllServicesButton(serviceName);
        }
        switchTo().window(1);
        return nextPage.cast(page(nextPage));
    }

    /**
     * Переходит в указанный сервис с помощью кнопки "Все сервисы" и раскрытия списка
     * всех сервисов ({@code serviceName} чувствителен к регистру).
     *
     * @param serviceName Название сервиса, в который нужно перейти.
     * @author Юрий Юрченко
     */
    private void goToServiceViaAllServicesButton(String serviceName) {
        $x("//ul[@class='services-suggest__list']//li[@class='services-suggest__list-item-more']").click();
        $x("//div[@class='services-more-popup__more-button']").click();
        $x("//span[@class='services-more-popup__item' and .='" + serviceName + "']").click();

    }

    /**
     * Переходит в указанный сервис, если он есть в списке быстрого доступа
     * ({@code serviceName} чувствителен к регистру).
     *
     * @param serviceName Название сервиса, в который нужно перейти.
     * @return {@code true}, если переход выполнен, {@code false} если сервиса с названием {@code serviceName}
     * нет в списке быстрого доступа.
     * @author Юрий Юрченко
     */
    private boolean goToServiceFast(String serviceName) {
        SelenideElement targetService = $x("//ul[@class='services-suggest__list']//li[.=('" + serviceName + "')]");
        if (targetService.exists()) {
            targetService.click();
            return true;
        }
        return false;
    }
}
