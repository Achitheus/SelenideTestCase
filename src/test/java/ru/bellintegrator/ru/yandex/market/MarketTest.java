package ru.bellintegrator.ru.yandex.market;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.ru.yandex.YandexMain;
import pages.ru.yandex.market.GoodsCategory;
import pages.ru.yandex.market.GoodsCategory.CheckBoxProcessType;
import ru.bellintegrator.BaseTest;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.assertionMode;
import static com.codeborne.selenide.Selenide.open;
import static helpers.Assertions.selenideAssertWithStep;

public class MarketTest extends BaseTest {
    public static final Logger log = LoggerFactory.getLogger(MarketTest.class);
    @RegisterExtension
    static ScreenShooterExtension screenshotEmAll = new ScreenShooterExtension(true);

    @Feature("Поиск товаров")
    @DisplayName("Соответствие результатов поиска установленному фильтру")
    @ParameterizedTest(name = "{displayName} : {arguments}")
    @MethodSource(value = "helpers.DataProvider#checkSearchResultsByEnumFilter")
    public void marketTest(String url, String service, String section, String category,
                           Map<String, Set<String>> enumFilters) {
        GoodsCategory goodsCategory =
                open(url, YandexMain.class)
                        .goToService(service)
                        .toSectionCategory(section, category, GoodsCategory.class)
                        .setEnumFilters(enumFilters, CheckBoxProcessType.MARK);

        marketCheckAllPages(goodsCategory, enumFilters);
    }

    //TODO возможно стоит этот метод обобщить, сделать универсальным и поместить в ПО
    @Step("Проверка товаров на всех страницах")
    public static void marketCheckAllPages(GoodsCategory goodsCategory, Map<String, Set<String>> enumFilters) {
        int infinityCyclePreventer = 0;
        do {
            infinityCyclePreventer++;

            ElementsCollection productNameEls = goodsCategory.getPageProductNames();
            log.trace("Названия товаров на {} странице: {}", infinityCyclePreventer, productNameEls.texts());

            SelenideElement badName = productNameEls.find(not(match("",
                    nameEl -> enumFilters.get("Производитель").stream().anyMatch(
                            brand -> nameEl.getText().toLowerCase().contains(brand.toLowerCase())))));
            boolean badNameExists = badName.exists();
            // Не забыть вернуть значение STRICT!!!
            assertionMode = SOFT;
            selenideAssertWithStep(badName,
                    (el, message) -> badNameExists ? el.shouldNot(exist.because(message), Duration.ZERO) : el,
                    badNameExists
                            ? "<<< FAIL >>> На стр. " + infinityCyclePreventer + " наименование товара \""
                            + badName.getText() + "\" не соответствует фильтру \"Производитель\": " + enumFilters.get("Производитель")
                            : "На стр. " + infinityCyclePreventer + " все названия товаров соответствуют фильтру \"Производитель\": "
                            + enumFilters.get("Производитель"));
            assertionMode = STRICT;
        } while (goodsCategory.nextPage() && infinityCyclePreventer < 1000);
        log.info("Обработано {} страниц товаров", infinityCyclePreventer);
    }


}
