package pages.ru.yandex.market;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import pages.BasePage;

import static com.codeborne.selenide.Selenide.*;

public class MarketMain {

    @Step("Переход в категорию товаров \"{category}\" внутри секции \"{section}\"")
    public <T extends BasePage> T toSectionCategory(String section, String category, Class<T> nextPage) {
        openCatalog();
        navigateToSection(section);
        clickCategory(category);

        return nextPage.cast(page(nextPage));
    }

    private void openCatalog() {
        $x("//*[@data-zone-name='catalog' and @data-baobab-name='catalog']").click();
    }

    private void navigateToSection(String sectionTitle) {
        SelenideElement requiredSection = $x("//*[@data-zone-name='catalog-content']//*[@role='tablist']//li[.//span[text()='" + sectionTitle + "']]");
        actions().moveToElement(requiredSection).perform();
        requiredSection.shouldBe(Condition.attribute("aria-selected", "true"));
    }

    private void clickCategory(String section) {
        $x("//*[@role='tabpanel']//a[text()='" + section + "']").click();
    }
}
