package pages.ru.ya;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.*;

public class YandexMain {
    @Step("Переход в сервис {serviceName}")
    public MarketMain goToService(String serviceName) {
        $x("//input[@id='text' and @aria-label='Запрос']").click();
        if(!goToServiceFast(serviceName)) {
            goToServiceViaAllServicesButton(serviceName);
        }
        return page(MarketMain.class);
    }

    private void goToServiceViaAllServicesButton(String serviceName) {
        $x("//ul[@class='services-suggest__list']//li[@class='services-suggest__list-item-more']").click();
        $x("//div[@class='services-more-popup__more-button']").click();
        $x("//span[@class='services-more-popup__item' and .='"+serviceName+"']").click();

    }

    private boolean goToServiceFast(String serviceName) {
        SelenideElement serviceButton = $x("//ul[@class='services-suggest__list']//li[.=('"+serviceName+"')]");
        if(serviceButton.exists())
            serviceButton.click();
        return serviceButton.exists();
    }
}
