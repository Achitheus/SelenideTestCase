package pages.ru.ya;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.*;

public class MarketMain {

    public <T extends BasePage> T toCategorySection(String category, String section, Class<T> nextPage) {
        openCatalog();
        navigateToCategory(category);
        goToSection(section);


        return nextPage.cast(page(nextPage));
    }

    private MarketMain openCatalog() {
        $x("//*[@data-zone-name='catalog' and @data-baobab-name='catalog']").click();
        return this;
    }

    private MarketMain navigateToCategory(String sectionTitle) {
        SelenideElement requiredSection = $x("//*[@data-zone-name='catalog-content']//*[@role='tablist']//li[.//span[text()='"+sectionTitle+"']]");
        actions().moveToElement(requiredSection).perform();
        requiredSection.shouldBe(Condition.attribute("aria-selected", "true"));
        return this;
    }

    private MarketMain goToSection(String section) {
        $x("//*[@role='tabpanel']//a[text()='" + section + "']").click();
        return this;
    }
}
