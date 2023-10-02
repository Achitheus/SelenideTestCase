package pages.ru.yandex.market;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import helpers.SelenideCustom;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;

import java.time.Duration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static helpers.SelenideCustom.metCondition;

public class GoodsCategory extends BasePage {

    public static final Logger log = LoggerFactory.getLogger(GoodsCategory.class);

    private final String productNamesSelector = "//*[@id='searchResults']//*[@data-auto='snippet-title-header']";

    private final String elementBelowTheGoodsSelector = "//noindex//*[@data-auto='creditDisclaimer']";

    @Step("Установка фильтров перечислений")
    public GoodsCategory setEnumFilters(Map<String, Set<String>> enumFilters, CheckBoxProcessType processType) {
        for (Map.Entry<String, Set<String>> enumFilter : enumFilters.entrySet()) {
            setEnumFilter(enumFilter.getKey(), processType, enumFilter.getValue());
        }
        return this;
    }

    @Step("Установка фильтра перечислений \"{textInFilterTitle}\" значениями: {targets}")
    public GoodsCategory setEnumFilter(String textInFilterTitle, CheckBoxProcessType processType, Set<String> targets) {
        Set<String> mutableTargets = new HashSet<>(targets);
        SelenideElement filter = getFilterByTextInTitle(textInFilterTitle);
        processAvailableCheckBoxes(filter, mutableTargets, processType, false);
        if (mutableTargets.isEmpty())
            return this;
        expandCheckboxListSmart(filter);
        if (soCalledVirtuosoDataScrollerIsEnabled(filter)) {
            log.debug("Обнаружен асинхронный скроллер в фильтре \"{}\"", textInFilterTitle);
            processEnumFilterWithSearchField(filter, mutableTargets, processType);
        } else {
            log.debug("Асинхронный скроллер в фильтре \"{}\" не обнаружен", textInFilterTitle);
            processAvailableCheckBoxes(filter, mutableTargets, processType, true);
        }
        return this;
    }

    public ElementsCollection getPageProductNames() {
        waitGoodsLoading();
        scrollToBottom();
        return $$x(productNamesSelector);
    }

    public boolean nextPage() {
        SelenideElement nextButton = $x("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@data-baobab-name='next']//span");
        if (!nextButton.exists()) {
            return false;
        }
        nextButton.click();
        return true;
    }

    private void processAvailableCheckBoxes(SelenideElement filter, Set<String> mutableTargets, CheckBoxProcessType processType, boolean strictMode) {
        ElementsCollection optionList = filter.$$x(".//*[@data-zone-name = 'FilterValue']")
                .shouldHave(sizeGreaterThan(0));
        log.debug("В текущем фильтре обнаружено {} чекбоксов. Обрабатываю", optionList.size());
        for (Iterator<String> iterator = mutableTargets.iterator(); iterator.hasNext(); ) {
            String target = iterator.next();
            SelenideElement checkbox = optionList.find(text(target));
            if (strictMode) {
                clickCheckboxIfNecessary(checkbox, processType);
                continue;
            }
            if (checkbox.exists()) {
                clickCheckboxIfNecessary(checkbox, processType);
                iterator.remove();
            }
        }
    }

    private void scrollToBottom() {
        $x(elementBelowTheGoodsSelector).scrollIntoView(false);
    }

    private void processEnumFilterWithSearchField(SelenideElement filter, Set<String> targetSet, CheckBoxProcessType processType) {
        log.info("Обрабатываю фильтр с помощью поискового поля");
        SelenideElement filterSearchField = filter.$x(".//input[@type='text']");
        for (String target : targetSet) {
            filterSearchField.click();
            filterSearchField.clear();
            filterSearchField.sendKeys(target);
            SelenideElement checkbox = filter.$x(".//*[@data-zone-name = 'FilterValue'][1]")
                    .shouldHave(partialText(target));
            clickCheckboxIfNecessary(checkbox, processType);
        }
    }

    private void waitGoodsLoading() {
        SelenideElement spinner = $x("//*[@data-grabber='SearchSerp']//*[@data-auto='spinner']");
        log.trace("Ожидаю появления спиннера загрузки товаров");
        if (spinner.execute(SelenideCustom.metCondition(el -> el.should(appear, Duration.ofSeconds(1))))) {
            log.trace("Спиннер загрузки товаров появился");
            spinner.should(disappear);
        } else {
            log.trace("Спиннер загрузки товаров не появился");
        }

    }

    private SelenideElement getFilterByTextInTitle(String textInTitle) {
        SelenideElement filter = $x("//*[@id='searchFilters']//fieldset[ .//legend[contains(., '" + textInTitle + "')]]")
                .should(exist);
        log.info("Найден фильтр по названию: \"{}\"", textInTitle);
        return filter;
    }

    private void expandCheckboxList(SelenideElement filter) {
        filter.scrollIntoView(true);
        log.debug("Нажимаю \"развернуть/свернуть\"");
        filter.$(By.tagName("button")).shouldBe(interactable).click();
    }

    private void expandCheckboxListSmart(SelenideElement filter) {
        expandCheckboxList(filter);
        log.debug("На случай если список свернется (баг): " +
                "жду повторного появления кнопки \"развернуть\"");
        SelenideElement expandButton = filter.$(By.tagName("button"));
        if (!expandButton.execute(metCondition(el -> el.shouldBe(visible, Duration.ofSeconds(3))))) {
            log.debug("Кнопка \"развернуть/свернуть\" не появилась");
            return;
        }
        if (Boolean.parseBoolean(expandButton.attr("aria-expanded"))) {
            log.debug("Появилась кнопка \"развернуть/свернуть\", но она уже нажата");
        } else {
            log.debug("Кнопка \"развернуть\" появилась и она не нажата. Нажимаю");
            filter.$(By.tagName("button")).click();
        }

    }

    private boolean soCalledVirtuosoDataScrollerIsEnabled(SelenideElement filter) {
        return filter.$x(".//*[@data-virtuoso-scroller='true']").exists();
    }

    private void clickCheckboxIfNecessary(SelenideElement option, CheckBoxProcessType processType) {
        if (processType.equals(CheckBoxProcessType.UNMARK)) {
            if (checkBoxIsMarked(option)) option.shouldBe(interactable).click();
        } else {
            if (!checkBoxIsMarked(option)) option.shouldBe(interactable).click();
        }

    }

    private boolean checkBoxIsMarked(SelenideElement option) {
        return Boolean.parseBoolean(option.$x(".//input").getAttribute("checked"));
    }

    public enum CheckBoxProcessType {
        /**
         * Экземпляр перечисления, означающий, что галочки на чекбоксы нужно поставить, а не снять
         */
        MARK,
        /**
         * Экземпляр перечисления, означающий, что с чекбоксов галочки нужно снять, а не поставить
         */
        UNMARK
    }
}
