package helpers;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.match;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Configuration.assertionMode;
import static helpers.AllureCustom.markOuterStepAsFailedAndStop;
import static helpers.AllureCustom.stepSoftAssert;
import static io.qameta.allure.Allure.step;

public class PageableChecker<T extends Pageable> {
    private boolean generalCheckFailed = false;
    private boolean checkAllPages = false;
    private final List<Check<? extends ElementsCollection>> checkList = new ArrayList<>();
    private final T target;

    public PageableChecker(T target) {
        this.target = target;
    }

    public PageableChecker<T> checkAllPages(boolean value) {
        this.checkAllPages = value;
        return this;
    }

    @Step("Постраничная проверка")
    public T start() {
        int infCyclePreventer = 0;
        do {
            infCyclePreventer++;
            step("Страница " + infCyclePreventer, this::processPageCheck);
        } while (!checkList.isEmpty() && infCyclePreventer < 1000 && target.nextPage());
        if (generalCheckFailed) {
            markOuterStepAsFailedAndStop();
        }
        return target;
    }

    private void processPageCheck() {
        boolean pageCheckFailed = false;
        ListIterator<Check<? extends ElementsCollection>> checksIter = checkList.listIterator();
        while (checksIter.hasNext()) {
            Check<? extends ElementsCollection> check = checksIter.next();
            check.perform();
            assertionMode = SOFT;
            stepSoftAssert(check.passMessage(), check.failedMessage(),
                    failedMess -> check.badElement.shouldNot(exist.because(failedMess), Duration.ZERO),
                    check.failed);
            assertionMode = STRICT;
            if (!check.failed) {
                continue;
            }
            pageCheckFailed = true;
            generalCheckFailed = true;
            if (checkAllPages) {
                check.failed = false;
            } else {
                checksIter.remove();
            }
        }
        if (pageCheckFailed) {
            markOuterStepAsFailedAndStop();
        }
    }

    public <R extends ElementsCollection> PageableChecker<T> addCheckThatEachElement(String continueMethodName, Function<T, R> elementsSupplier,
                                                                                     Predicate<WebElement> condition) {
        checkList.add(new Check<>(continueMethodName, elementsSupplier, condition));
        return this;
    }

    private class Check<R extends ElementsCollection> {
        private final String checkDescr;
        private boolean failed = false;
        private SelenideElement badElement;
        private String badElementText;
        private final Predicate<WebElement> condition;
        private final Function<T, R> getElements;

        private Check(String checkDescr, Function<T, R> getElements, Predicate<WebElement> condition) {
            this.checkDescr = checkDescr;
            this.getElements = getElements;
            this.condition = condition;
        }

        private void perform() {
            badElement = getElements.apply(target).find(not(match("", condition)));
            if (badElement.exists()) {
                failed = true;
                badElementText = badElement.getText();
            }
        }

        private String passMessage() {
            if (failed)
                return null;
            return "Каждый элемент " + checkDescr;
        }

        private String failedMessage() {
            if (!failed)
                return null;
            return "Элемент \"" + badElementText + "\" не " + checkDescr;
        }
    }
}
