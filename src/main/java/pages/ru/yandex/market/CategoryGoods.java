package pages.ru.yandex.market;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static helpers.SelenideCustom.metCondition;

/**
 * Класс, предоставляющий функционал для взаимодействия со страницей товаров.
 *
 * @author Юрий Юрченко
 */
public class CategoryGoods extends MarketHeader {

    public static final Logger log = LoggerFactory.getLogger(CategoryGoods.class);

    private final String productNamesSelector = "//*[@id='searchResults']//*[@data-auto='snippet-title-header']";

    private final String elementBelowTheGoodsSelector = "//noindex//*[@data-auto='creditDisclaimer']";

    /**
     * Обрабатывает чекбоксы фильтров перечислений в режиме {@code processMode}, после чего
     * ждет загрузки товаров.
     *
     * @param enumFilters Фильтры перечислений в формате ключ - название фильтра, значение - набор чекбоксов.
     * @param processMode Режим обработки чекбоксов.
     * @return Текущий объект данного класса.
     * @author Юрий Юрченко
     * @see CheckboxProcessMode
     */
    @Step("Установка фильтров перечислений")
    public CategoryGoods setEnumFilters(Map<String, Set<String>> enumFilters, CheckboxProcessMode processMode) {
        for (Map.Entry<String, Set<String>> enumFilter : enumFilters.entrySet()) {
            setEnumFilterWithoutWait(enumFilter.getKey(), enumFilter.getValue(), processMode);
        }
        waitGoodsLoading();
        return this;
    }

    /**
     * Отмечает чекбоксы фильтров перечислений, если они еще не отмечены, после чего
     * ждет загрузки товаров.
     *
     * @param enumFilters Фильтры перечислений в формате ключ - название фильтра, значение - набор чекбоксов.
     * @return Текущий объект данного класса.
     * @author Юрий Юрченко
     */
    public CategoryGoods setEnumFilters(Map<String, Set<String>> enumFilters) {
        return setEnumFilters(enumFilters, CheckboxProcessMode.MARK);
    }

    /**
     * Обрабатывает чекбоксы фильтра перечислений в режиме {@code processMode}.
     * После обработки чекбоксов ожидает загрузки товаров, поэтому, если фильтров несколько,
     * предпочтительнее использовать {@link #setEnumFilters(Map, CheckboxProcessMode)}
     *
     * @param textInFilterTitle Текст в названии фильтра перечислений (чувствительный к регистру).
     * @param processMode       Режим обработки чекбоксов (отметить или снять отметку).
     * @param targets           Набор названий чекбоксов, подлежащих обработке.
     * @return Текущий объект данного класса.
     * @author Юрий Юрченко
     * @see CheckboxProcessMode
     */
    public CategoryGoods setEnumFilter(String textInFilterTitle, Set<String> targets, CheckboxProcessMode processMode) {
        setEnumFilterWithoutWait(textInFilterTitle, targets, processMode);
        waitGoodsLoading();
        return this;
    }

    /**
     * Отмечает чекбоксы фильтра перечислений, если они еще не отмечены.
     * После обработки чекбоксов ожидает загрузки товаров, поэтому, если фильтров несколько,
     * предпочтительнее использовать {@link #setEnumFilters(Map)}
     *
     * @param textInFilterTitle Текст в названии фильтра перечислений (чувствительный к регистру).
     * @param targets           Набор названий чекбоксов, подлежащих обработке.
     * @return Текущий объект данного класса.
     * @author Юрий Юрченко
     */
    public CategoryGoods setEnumFilter(String textInFilterTitle, Set<String> targets) {
        return setEnumFilter(textInFilterTitle, targets, CheckboxProcessMode.MARK);
    }

    /**
     * Обрабатывает чекбоксы фильтра перечислений в режиме {@code processMode}, чувствителен к регистру.
     *
     * @param textInFilterTitle Текст, содержащийся в названии фильтра перечислений.
     * @param targets           Набор названий чекбоксов, подлежащих обработке.
     * @param processMode       Режим обработки чекбоксов.
     * @author Юрий Юрченко
     */
    @Step("Обработка в фильтре перечислений \"{textInFilterTitle}\" чекбоксов: {targets}")
    private void setEnumFilterWithoutWait(String textInFilterTitle, Set<String> targets, CheckboxProcessMode processMode) {
        Set<String> mutableTargets = new HashSet<>(targets);
        SelenideElement filter = getFilterBy(textInFilterTitle);
        processAvailableCheckBoxes(filter, mutableTargets, processMode, false);
        if (mutableTargets.isEmpty())
            return;
        expandCheckboxListAvoidingBug(filter);
        if (soCalledDataVirtuosoScrollerIsEnabled(filter)) {
            log.debug("Обнаружен асинхронный скроллер в фильтре \"{}\"", textInFilterTitle);
            processEnumFilterWithSearchField(filter, mutableTargets, processMode);
        } else {
            log.debug("Асинхронный скроллер в фильтре \"{}\" не обнаружен", textInFilterTitle);
            processAvailableCheckBoxes(filter, mutableTargets, processMode, true);
        }
    }

    /**
     * Скроллит вниз, затем возвращает набор элементов, содержащих названия товаров.
     *
     * @return Набор элементов, содержащих названия товаров.
     * @author Юрий Юрченко
     */
    public ElementsCollection getPageProductNames() {
        scrollToBottom();
        return $$x(productNamesSelector);
    }

    /**
     * Нажимает кнопку "next page", если она присутствует на странице, затем ждет загрузки товаров.
     *
     * @return {@code true}, если кнопка "next page" была нажата, иначе {@code false}.
     * @author Юрий Юрченко
     */
    public boolean nextPage() {
        SelenideElement nextButton = $x("//div[@data-apiary-widget-name=\"@marketfront/SearchPager\"]//div[@data-baobab-name='next']//span");
        if (!nextButton.exists()) {
            return false;
        }
        nextButton.click();
        waitGoodsLoading();
        return true;
    }

    /**
     * Ищет на странице в {@code filter} чекбоксы по названиям из {@code mutableTargets}
     * и обрабатывает их в соответствии с {@code processMode}.
     * Чувствителен к регистру, проверяет на точное соответствие. <p>
     * Предполагается, что метод следует вызывать дважды: до раскрытия списка чекбоксов
     * (со значением {@code strictMode = false}) и после (со {@code strictMode = true}).
     *
     * @param filter         Фильтр, в котором обрабатываются чекбоксы.
     * @param mutableTargets Названия чекбоксов, которые следует обработать.
     * @param processMode    Режим обработки чекбоксов.
     * @param strictMode     Режим работы. При {@code false}, метод не падает с ошибкой, когда среди
     *                       {@code mutableTargets} обнаруживается название для которого
     *                       на странице нет соответствующего чекбокса.
     * @author Юрий Юрченко
     */
    private void processAvailableCheckBoxes(SelenideElement filter, Set<String> mutableTargets, CheckboxProcessMode processMode, boolean strictMode) {
        ElementsCollection checkboxList = filter.$$x(".//*[@data-zone-name = 'FilterValue']").shouldHave(sizeGreaterThan(0));
        if (strictMode)
            checkboxList.shouldHave(containExactTextsCaseSensitive(mutableTargets.toArray(new String[0])));
        log.debug("В текущем фильтре обнаружено {} чекбоксов. Обрабатываю", checkboxList.size());
        for (SelenideElement checkbox : checkboxList) {
            String checkboxName = checkbox.getText();
            String target = mutableTargets.stream().filter(
                    checkboxName::equals).findFirst().orElse("");
            if (target.isEmpty()) {
                continue;
            }
            clickCheckboxIfNecessary(checkbox, processMode);
            mutableTargets.remove(target);
            if (mutableTargets.isEmpty()) {
                log.info("Все заданные чекбоксы обработаны");
                return;
            }
        }
    }

    /**
     * С помощью поля поиска ищет в {@code filter} чекбоксы по названиям
     * из {@code mutableTargets} и обрабатывает их в соответствии с {@code processMode}.
     * Чувствителен к регистру, игнорирует множественные whitespaces.
     *
     * @param filter      Фильтр, в котором обрабатываются чекбоксы.
     * @param targetSet   Названия чекбоксов, которые следует обработать.
     * @param processMode Режим обработки чекбоксов (отметить или снять отметку).
     * @author Юрий Юрченко
     */
    private void processEnumFilterWithSearchField(SelenideElement filter, Set<String> targetSet, CheckboxProcessMode processMode) {
        log.info("Обрабатываю фильтр с помощью поискового поля");
        SelenideElement filterSearchField = filter.$x(".//input[@type='text']");
        for (String target : targetSet) {
            filterSearchField.click();
            filterSearchField.clear();
            filterSearchField.sendKeys(target);
            SelenideElement checkbox = filter.$x(".//*[@data-zone-name = 'FilterValue'][1]")
                    .shouldHave(exactTextCaseSensitive(target));
            clickCheckboxIfNecessary(checkbox, processMode);
        }
    }

    /**
     * Производит скролл до объекта, расположенного ниже товаров
     * (при этом в DOM-е появляются все товары страницы).
     *
     * @author Юрий Юрченко
     */
    private void scrollToBottom() {
        $x(elementBelowTheGoodsSelector).scrollIntoView(false);
    }

    /**
     * Дожидается (мягко) появления спиннера загрузки товаров и, в случае успеха, ждет его исчезновения (жестко).
     *
     * @author Юрий Юрченко
     */
    private void waitGoodsLoading() {
        SelenideElement spinner = $x("//*[@data-grabber='SearchSerp']//*[@data-auto='spinner']");
        log.trace("Ожидаю появления спиннера загрузки товаров");
        if (spinner.execute(metCondition(el -> el.should(appear, Duration.ofSeconds(1))))) {
            log.trace("Спиннер загрузки товаров появился");
            spinner.should(disappear);
        } else {
            log.trace("Спиннер загрузки товаров не появился");
        }
    }

    /**
     * Находит на странице фильтр, содержащий в названии {@code textInTitle} (поиск чувствителен к регистру).
     *
     * @param textInTitle Текст, содержащийся в названии фильтра.
     * @return Фильтр, найденный по {@code textInTitle}.
     * @author Юрий Юрченко
     */
    private SelenideElement getFilterBy(String textInTitle) {
        SelenideElement filter = $x("//*[@id='searchFilters']//fieldset[ .//legend[contains(., '" + textInTitle + "')]]")
                .should(exist);
        log.info("Найден фильтр по названию: \"{}\"", textInTitle);
        return filter;
    }

    /**
     * Разворачивает список чекбоксов у перечислимого {@code filter}.
     *
     * @param filter Фильтр перечисления, у которого нужно развернуть список чекбоксов.
     * @author Юрий Юрченко
     */
    private void expandCheckboxList(SelenideElement filter) {
        filter.scrollIntoView(true);
        log.debug("Нажимаю \"развернуть/свернуть\"");
        filter.$(By.tagName("button")).shouldBe(interactable).click();
    }

    /**
     * Делает то же, что и {@link #expandCheckboxList(SelenideElement)}, но после этого еще ожидает повторного
     * появления кнопки "развернуть/свернуть" (баг) и, если список чекбоксов все же свернулся, еще раз его разворачивает.
     *
     * @param filter Фильтр перечисления, у которого нужно развернуть список чекбоксов.
     * @author Юрий Юрченко
     */
    private void expandCheckboxListAvoidingBug(SelenideElement filter) {
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

    /**
     * Сообщает о наличии в {@code filter} AJAX-овского "датаВиртуозоСкроллера".
     *
     * @param filter Фильтр, проверяемый на наличие AJAX скроллера.
     * @return {@code true}, если AJAX скроллер обнаружен, иначе {@code false}.
     * @author Юрий Юрченко
     */
    private boolean soCalledDataVirtuosoScrollerIsEnabled(SelenideElement filter) {
        return filter.$x(".//*[@data-virtuoso-scroller='true']").exists();
    }

    /**
     * Кликает по чекбоксу при необходимости, которая зависит от заданного {@code processMode} и текущего
     * состояния чекбокса.
     *
     * @param option      Чекбокс, по которому, вероятно, следует кликнуть.
     * @param processMode Режим обработки чекбокса.
     * @author Юрий Юрченко
     */
    private void clickCheckboxIfNecessary(SelenideElement option, CheckboxProcessMode processMode) {
        switch (processMode) {
            case UNMARK:
                if (checkBoxIsMarked(option)) option.shouldBe(interactable).click();
                break;
            case MARK:
                if (!checkBoxIsMarked(option)) option.shouldBe(interactable).click();
                break;
            case CHANGE:
                option.shouldBe(interactable).click();
                break;
        }
    }

    /**
     * Предоставляет информацию о состоянии чекбокса: возвращает {@code true},
     * если {@code checkbox} отмечен, иначе {@code false}.
     *
     * @param checkbox Чекбокс, состояние которого нужно определить.
     * @return {@code true}, если чекбокс отмечен, иначе {@code false}.
     * @author Юрий Юрченко
     */
    private boolean checkBoxIsMarked(SelenideElement checkbox) {
        return Boolean.parseBoolean(checkbox.$x(".//input").getAttribute("checked"));
    }

    /**
     * Режимы обработки чекбоксов.
     *
     * @author Юрий Юрченко
     */
    public enum CheckboxProcessMode {
        /**
         * Режим обработки, при котором на чекбоксы отметки ставятся.
         * Если чекбокс уже отмечен, его состояние не меняется.
         */
        MARK,
        /**
         * Режим обработки, при котором с чекбоксов отметки снимаются.
         * Если чекбокс не отмечен, его состояние не меняется.
         */
        UNMARK,
        /**
         * Режим обработки, при котором состояния чекбоксов меняются на противоположные.
         */
        CHANGE
    }
}
