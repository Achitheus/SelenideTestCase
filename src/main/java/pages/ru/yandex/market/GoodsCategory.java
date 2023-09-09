package pages.ru.yandex.market;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;

import java.time.Duration;
import java.util.*;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static helpers.SelenideCustom.metCondition;

public class GoodsCategory extends BasePage {

    public static final Logger log = LoggerFactory.getLogger(GoodsCategory.class);

    private final String productNamesSelector = "//*[@data-autotest-id='product-snippet']//*[@data-auto='snippet-title-header']";

    public GoodsCategory setEnumFilters(Map<String, List<String>> enumFilters) {
        for (Map.Entry<String, List<String>> enumFilter : enumFilters.entrySet()) {
            setEnumFilterWithoutWait(enumFilter.getKey(), CheckBoxProcessType.MARK, enumFilter.getValue());
        }
        waitUntilGoodsLoaded();
        return this;
    }

    public GoodsCategory setEnumFilter(String textInFilterTitle, CheckBoxProcessType processType, List<String> targets) {
        setEnumFilterWithoutWait(textInFilterTitle, processType, targets);
        waitUntilGoodsLoaded();
        return this;
    }

    public ElementsCollection getPageProductNames() {
        scrollToBottom();
        return $$x(productNamesSelector);
    }

    public boolean nextPage() {
        SelenideElement nextButton = $x("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@data-baobab-name='next']//span");
        if (nextButton.execute(metCondition(
                button -> button.shouldBe(visible, Duration.ofSeconds(2))))) {

            nextButton.click();
            waitUntilGoodsLoaded();
            return true;
        }
        return false;
    }

    private void scrollToBottom() {
        $x("//*[@data-auto=\"pagination-page\"]").scrollIntoView(false);
    }

    @Step("Установка фильтра перечислений: {textInFilterTitle} значениями: {targets}")
    private void setEnumFilterWithoutWait(String textInFilterTitle, CheckBoxProcessType processType, List<String> targets) {
        SelenideElement filter = getFilterByTextInTitle(textInFilterTitle);
        Set<String> targetSet = new HashSet<>(targets);
        processAvailableCheckBoxes(filter, targetSet, processType);
        if (targetSet.isEmpty()) {
            return;
        }
        log.trace("Не все указанные чекбоксы найдены - продолжаю работу");

        expandCheckboxListSmart(filter);
        if (soCalledVirtuosoDataScrollerIsEnabled(filter)) {
            log.debug("Обнаружен асинхронный скроллер в фильтре \"{}\"", textInFilterTitle);
            processEnumFilterWithSearchField(filter, targetSet, processType);
        } else {
            log.debug("Асинхронный скроллер в фильтре \"{}\" не обнаружен", textInFilterTitle);
            processAvailableCheckBoxes(filter, targetSet, processType);
        }

        Assertions.assertTrue(targetSet.isEmpty(),
                "В фильтре \"" + textInFilterTitle + "\" не найдены значения:\n" + targetSet);
    }

    private void processAvailableCheckBoxes(SelenideElement filter, Set<String> targetSet, CheckBoxProcessType processType) {
        ElementsCollection optionList = filter.$$x(".//*[@data-zone-name = 'FilterValue']").shouldHave(sizeGreaterThan(0));
        log.debug("В текущем фильтре обнаружено {} чекбоксов. Обрабатываю", optionList.size());
        for (SelenideElement option : optionList) {
            String optionTitle = option.$x(".//span[text()]").getText().toLowerCase();
            String target = targetSet.stream().filter(targ -> optionTitle.contains(targ.toLowerCase())).findFirst().orElse("");
            if (!target.isEmpty()) {
                if (checkBoxShouldBeToggled(option, processType)) {
                    option.$x(".//label").shouldBe(interactable).click();
                }
                targetSet.remove(target);
                if (targetSet.isEmpty()) {
                    log.info("Все заданные чекбоксы обработаны");
                    return;
                }
            }
        }
    }

    private void processEnumFilterWithSearchField(SelenideElement filter, Set<String> targetSet, CheckBoxProcessType processType) {
        log.info("Начинаю обработку фильтра с помощью поискового поля");
        SelenideElement filterSearchField = filter.$x(".//input[@type='text']");
        for (Iterator<String> iterator = targetSet.iterator(); iterator.hasNext(); ) {
            String target = iterator.next();
            filterSearchField.click();
            filterSearchField.clear();
            filterSearchField.sendKeys(target);

            SelenideElement option = filter.$x(".//*[@data-zone-name = 'FilterValue'][1]")
                    .shouldHave(partialText(target));
            if (checkBoxShouldBeToggled(option, processType)) {
                option.click();
            }
            iterator.remove();
        }
    }

    private void waitUntilGoodsLoaded() {
        Configuration.pollingInterval = 50;
        $x("//*[@data-grabber='SearchSerp']//*[@data-auto='spinner']")
                .should(appear)
                .should(disappear);
    }

    private SelenideElement getFilterByTextInTitle(String textInTitle) {
        SelenideElement filter = $x("//*[@id='searchFilters']//fieldset[ .//legend[contains(., '" + textInTitle + "')]]")
                .should(exist);
        log.info("Найден фильтр по названию: \"{}\"", textInTitle);
        return filter;
    }

    private void expandCheckboxList(SelenideElement filter) {
        log.debug("Нажимаю \"развернуть\"");
        filter.$(By.tagName("button")).scrollIntoView(false).click();
    }

    private void expandCheckboxListSmart(SelenideElement filter) {
        expandCheckboxList(filter);
        log.debug("На случай если список свернется (баг): " +
                "жду повторное появления кнопки \"развернуть\"");
        SelenideElement expandButton = filter.$(By.tagName("button"));
        if (!expandButton.execute(metCondition(el -> el.shouldBe(visible, Duration.ofSeconds(6))))) {
            log.debug("Кнопка \"развернуть/свернуть\" не появилась");
            return;
        }

        if (Boolean.parseBoolean(expandButton.attr("aria-expanded"))) {
            log.debug("Появилась кнопка \"свернуть\"");
            return;
        }
        log.debug("Кнопка \"развернуть\" появилась и она не нажата. Нажимаю");
        filter.$(By.tagName("button")).click();

    }

    private boolean soCalledVirtuosoDataScrollerIsEnabled(SelenideElement filter) {
        return filter.$x(".//*[@data-virtuoso-scroller='true']").exists();
    }

    private boolean checkBoxShouldBeToggled(SelenideElement option, CheckBoxProcessType processType) {
        if (processType.equals(CheckBoxProcessType.UNMARK))
            return checkBoxIsMarked(option);
        else
            return !checkBoxIsMarked(option);
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
