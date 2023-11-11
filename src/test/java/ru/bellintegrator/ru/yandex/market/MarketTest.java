package ru.bellintegrator.ru.yandex.market;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.ru.yandex.YandexMain;
import pages.ru.yandex.market.CategoryGoods;
import pages.ru.yandex.market.CategoryGoods.CheckboxProcessMode;
import pages.ru.yandex.market.MarketMain;
import ru.bellintegrator.BaseTest;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.assertionMode;
import static com.codeborne.selenide.Selenide.open;
import static helpers.AllureCustom.markOuterStepAsFailedAndStop;
import static helpers.AllureCustom.stepSoftAssert;
import static helpers.Assertions.selenideAssertWithStep;

public class MarketTest extends BaseTest {
    public static final Logger log = LoggerFactory.getLogger(MarketTest.class);

    /**
     * Тест-кейс: <p>
     * 1. Открыть браузер и развернуть на весь экран. <p>
     * 2. Перейти на {@code url}<p>
     * 3. Нажать на строку поиска -> Кликнуть по Маркет <p>
     * 4. Перейти в Каталог -> Навести курсор на раздел  Электроника <p>
     * 5. Выбрать раздел Смартфоны <p>
     * 6. Задать параметр «Производитель» Apple. <p>
     * 7. Дождаться результатов поиска. <p>
     * 8. Убедиться, что в выборку попали только iPhone. Если страниц несколько – проверить все. <p>
     * Тест должен работать для любого производителя из списка:
     * ASUS, Black Shark, OnePlus, Google, Seals.
     *
     * @param url           Начальная страница с сервисами, на которую следует перейти.
     * @param service       Сервис, в который следует перейти.
     * @param section       Секция, внутри которой содержится нужная {@code category}.
     * @param category      Категория товаров, в которую следует перейти.
     * @param enumFilters   Фильтры перечислений в формате Map<Название фильтра, набор названий чекбоксов>.
     * @param enumCheckSets Наборы проверочных слов для проверки соответствия товаров установленным фильтрам {@code enumFilters}.
     * @author Юрий Юрченко
     */
    @Feature("Поиск товаров")
    @DisplayName("Соответствие результатов поиска установленным фильтрам")
    @ParameterizedTest(name = "{displayName} : {arguments}")
    @MethodSource(value = "helpers.DataProvider#checkSearchResultsByEnumFilter")
    public void marketTest(String url, String service, String section, String category,
                           Map<String, Set<String>> enumFilters, Map<String, Set<String>> enumCheckSets) {
        CategoryGoods categoryGoods =
                open(url, YandexMain.class)
                        .goToService(service, MarketMain.class)
                        .toSectionCategory(section, category)
                        .setEnumFilters(enumFilters, CheckboxProcessMode.MARK);

        marketCheckAllPages(categoryGoods, enumCheckSets);
    }

    /**
     * Проходит по всем страницам, проверяя каждую на соответствие товаров (не чувствительно
     * к регистру) наборам проверочных слов {@code enumCheckSets}.
     *
     * @param categoryGoods Пэйдж обджект страницы (с пагинацией) товаров, подлежащих проверке.
     * @param enumCheckSets Наборы для проверки соответствия установленным фильтрам перечислений в
     *                      формате Map<Название фильтра, набор проверочных слов>.
     * @author Юрий Юрченко
     */
    // TODO превратить метод во что-то реюзабельное и вменяемое
    @Step("Проверка товаров на всех страницах")
    public static void marketCheckAllPages(CategoryGoods categoryGoods, Map<String, Set<String>> enumCheckSets) {
        int infCyclePreventer = 0;
        boolean badNameExists;
        do {
            infCyclePreventer++;
            ElementsCollection productNameEls = categoryGoods.getPageProductNames();
            log.debug("Страница {}. Число товаров: {}", infCyclePreventer, productNameEls.size());
            log.trace("Названия товаров на {} странице: {}", infCyclePreventer, productNameEls.texts());

            SelenideElement badName = productNameEls.find(not(match("",
                    nameEl -> enumCheckSets.get("Производитель").stream().anyMatch(
                            brand -> nameEl.getText().toLowerCase().contains(brand.toLowerCase())))));
            badNameExists = badName.exists();
            // Не забыть вернуть значение STRICT!!!
            assertionMode = SOFT;
            stepSoftAssert("На стр. " + infCyclePreventer + " все названия товаров соответствуют фильтру \"Производитель\". "
                            + "Слова проверки: " + enumCheckSets.get("Производитель"),
                    "На стр. " + infCyclePreventer + " наименование товара \"" +(badNameExists ? badName.getText() : "")
                            + "\" не соответствует фильтру \"Производитель\". Слова проверки: " + enumCheckSets.get("Производитель"),
                    (failedMess) -> badName.shouldNot(exist.because(failedMess), Duration.ZERO),
                    badNameExists);
            assertionMode = STRICT;
        } while (categoryGoods.nextPage() && infCyclePreventer < 1000);
        if (badNameExists) {
            markOuterStepAsFailedAndStop();
        }
        log.info("Обработано {} страниц товаров", infCyclePreventer);
    }


}
