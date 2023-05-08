package pages.ru.ya;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.*;

public class YandexAllServices {
    @Step("Переход в сервис {serviceName}")
    public MarketMain goToService(String serviceName) {
        $x("//*[@aria-labelledby='services-big-item-market-title']").click();
        return page(MarketMain.class);
    }
}
