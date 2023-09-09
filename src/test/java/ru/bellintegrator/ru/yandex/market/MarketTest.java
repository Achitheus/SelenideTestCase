package ru.bellintegrator.ru.yandex.market;

import com.codeborne.selenide.ElementsCollection;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.ru.yandex.YandexMain;
import pages.ru.yandex.market.GoodsCategory;
import pages.ru.yandex.market.GoodsCategory.CheckBoxProcessType;
import ru.bellintegrator.BaseTest;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.allMatch;
import static com.codeborne.selenide.Selenide.open;

public class MarketTest extends BaseTest {
    public static final Logger log = LoggerFactory.getLogger(MarketTest.class);

    @Test
    public void marketTest() {
        String url = "https://ya.ru/";
        List<String> brands = List.of("samsung", "ark", "asus", "blackberry");
        String serviceTitle = "Маркет";
        GoodsCategory goodsCategory =
                open(url, YandexMain.class)
                        .goToService(serviceTitle)
                        .toCategorySection("Электроника", "Смартфоны", GoodsCategory.class)
                        .setEnumFilter("Производитель", CheckBoxProcessType.MARK, brands);
        for (int infinityCyclePreventer = 0; infinityCyclePreventer < 1000; infinityCyclePreventer++) {
            ElementsCollection productNameEls = goodsCategory.getPageProductNames();
            log.debug("Названия товаров на странице: {}", productNameEls.texts());
            productNameEls.should(allMatch("",
                    name -> brands.stream().anyMatch(
                            brand -> name.getText().toLowerCase().contains(brand.toLowerCase()))));
            if (!goodsCategory.nextPage()) {
                log.info("Обработано {} страниц товаров", infinityCyclePreventer + 1);
                break;
            }
        }
    }

    @Test
    public void marketTestShort() {
        String url = "https://market.yandex.ru/catalog--smartfony/26893750/list";
        List<String> brands = List.of("samsung", "ark", "asus", "blackberry");
        GoodsCategory goodsCategory =
                open(url, GoodsCategory.class)
                        .setEnumFilter("Производитель", CheckBoxProcessType.MARK, brands);
        for (int infinityCyclePreventer = 0; infinityCyclePreventer < 1000; infinityCyclePreventer++) {
            ElementsCollection productNameEls = goodsCategory.getPageProductNames();
            log.debug("Названия товаров на странице: {}", productNameEls.texts());
            productNameEls.should(allMatch("Все названия товаров должны содержать один из установленных в фильтре брэндов",
                    name -> brands.stream().anyMatch(
                            brand -> name.getText().toLowerCase().contains(brand.toLowerCase()))));
            if (!goodsCategory.nextPage()) {
                log.info("Обработано {} страниц товаров", infinityCyclePreventer + 1);
                break;
            }
        }
    }

}
