package ru.bellintegrator;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ru.ya.MarketSection;
import pages.ru.ya.YandexAllServices;

import static com.codeborne.selenide.Selenide.open;

public class MarketTest extends BaseTest {

    @Feature("Проверка фильтра маркета")
    @DisplayName("Проверка фильтра маркета по результатам поиска")
    //@ParameterizedTest(name = "{} : arguments")
    @Test
    public void marketTest() {
        String url = "https://yandex.ru/all";
        String serviceTitle = "Маркет";
        open(url, YandexAllServices.class).goToService(serviceTitle)
                .toCategorySection("электроника", "смартфоны", MarketSection.class);

    }
}
