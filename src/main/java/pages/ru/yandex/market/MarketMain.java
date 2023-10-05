package pages.ru.yandex.market;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import pages.BasePage;

import static com.codeborne.selenide.Selenide.*;

public class MarketMain {
    /**
     * Кликает по кнопке "открыть/закрыть каталог", наводит курсор на указанную {@code section},
     * затем в обновившемся блоке категорий кликает по указанной {@code category} (метод чувствителен к регистру).
     *
     * @param section  Секция, на которую следует навести курсор.
     * @param category Содержащаяся внутри {@code section} категория товаров, которую следует кликнуть.
     * @param nextPage Объект класса {@link Class}, с помощью которого создается возвращаемый данным методом объект.
     * @param <T>      Тип объекта, возвращаемого данным методом.
     * @return Пэйдж обджект типа {@code T}.
     * @author Юрий Юрченко
     */
    @Step("Переход в категорию товаров \"{category}\" внутри секции \"{section}\"")
    public <T extends BasePage> T toSectionCategory(String section, String category, Class<T> nextPage) {
        clickOnCatalogButton();
        navigateToSection(section);
        clickCategory(category);

        return nextPage.cast(page(nextPage));
    }

    /**
     * Кликает по кнопке "открыть/закрыть каталог".
     *
     * @author Юрий Юрченко
     */
    private void clickOnCatalogButton() {
        $x("//*[@data-zone-name='catalog' and @data-baobab-name='catalog']").click();
    }

    /**
     * Наводит курсор на указанную {@code sectionTitle} (чувствительно к регистру).
     *
     * @param sectionTitle Название секции, на которую следует навести курсор.
     * @author Юрий Юрченко
     */
    private void navigateToSection(String sectionTitle) {
        SelenideElement requiredSection = $x("//*[@data-zone-name='catalog-content']//*[@role='tablist']//li[.//span[text()='" + sectionTitle + "']]");
        actions().moveToElement(requiredSection).perform();
        requiredSection.shouldBe(Condition.attribute("aria-selected", "true"));
    }

    /**
     * Кликает по указанной категории товаров {@code category} (чувствительно к регистру)
     * внутри текущей секции.
     *
     * @param category Название категории товаров, по которой следует кликнуть.
     * @author Юрий Юрченко
     */
    private void clickCategory(String category) {
        $x("//*[@role='tabpanel']//a[text()='" + category + "']").click();
    }
}
