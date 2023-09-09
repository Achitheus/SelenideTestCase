package pages.ru.yandex.market;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import pages.BasePage;

import static com.codeborne.selenide.Selenide.*;

public class MarketMain {

    public <T extends BasePage> T toCategorySection(String category, String section, Class<T> nextPage) {
        openCatalog();
        navigateToCategory(category);
        clickSection(section);


        return nextPage.cast(page(nextPage));
    }

    private void openCatalog() {
        $x("//*[@data-zone-name='catalog' and @data-baobab-name='catalog']").click();
    }

    private void navigateToCategory(String sectionTitle) {
        SelenideElement requiredSection = $x("//*[@data-zone-name='catalog-content']//*[@role='tablist']//li[.//span[text()='" + sectionTitle + "']]");
        actions().moveToElement(requiredSection).perform();
        requiredSection.shouldBe(Condition.attribute("aria-selected", "true"));
    }

    private void clickSection(String section) {
        $x("//*[@role='tabpanel']//a[text()='" + section + "']").click();
    }
}
