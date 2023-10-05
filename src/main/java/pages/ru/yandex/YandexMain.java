package pages.ru.yandex;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import pages.ru.yandex.market.MarketMain;

import static com.codeborne.selenide.Selenide.*;

public class YandexMain {
    /**
     * Переходит в указанный {@code service} (чувствительно к регистру).
     *
     * @param serviceName Название сервиса, в который следует перейти.
     * @return // TODO с какой стати всегда MarketMain? Исправить следующим коммитом
     * @author Юрий Юрченко
     */
    @Step("Переход в сервис \"{serviceName}\"")
    public MarketMain goToService(String serviceName) {
        $x("//input[@id='text' and @aria-label='Запрос']").click();
        if (!goToServiceFast(serviceName)) {
            goToServiceViaAllServicesButton(serviceName);
        }
        switchTo().window(1);
        return page(MarketMain.class);
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
        if (targetService.exists())
            targetService.click();
        return targetService.exists();
    }
}
