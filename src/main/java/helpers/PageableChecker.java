package helpers;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Configuration.assertionMode;
import static helpers.AllureCustom.markOuterStepAsFailedAndStop;
import static helpers.AllureCustom.stepSoftAssert;
import static io.qameta.allure.Allure.addAttachment;
import static io.qameta.allure.Allure.step;

public class PageableChecker<T extends Pageable> {
    private boolean pageableCheckFailed = false;
    private boolean checkAllPages = false;
    private final T target;
    private final List<Check<? extends ElementsCollection>> checkList;

    public PageableChecker(T target) {
        this.target = target;
        checkList = new ArrayList<>();
    }

    public PageableChecker<T> checkAllPages(boolean value) {
        this.checkAllPages = value;
        return this;
    }

    public T start() {
        step("Постраничная проверка. Режим " + (checkAllPages
                        ? "eager (чеки не скипаются, проверяются все страницы)"
                        : "lazy (упавший чек на последующих стр. пропускается)"),
                () -> {
                    if (checkList.isEmpty()) {
                        throw new RuntimeException("Checklist is empty");
                    }
                    int infCyclePreventer = 0;
                    do {
                        infCyclePreventer++;
                        step("Страница " + infCyclePreventer + (checkAllPages ? "" : ". Активных проверок: " + checkList.size()),
                                this::processPageCheck);
                    } while (!checkList.isEmpty() && infCyclePreventer < 1000 && target.nextPage());
                    if (pageableCheckFailed) {
                        markOuterStepAsFailedAndStop();
                    }
                }
        );
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
                    failedMess -> {
                        check.badElementList.get(0).shouldNot(exist.because(failedMess), Duration.ZERO);
                        if (check.failed && check.badElementTextList.size() > 1) {
                            addAttachment("bad elements", "text/plain",
                                    check.badElementTextList.toString().replaceAll(",", "\n"),
                                    ".txt");
                        }
                    },
                    check.failed);
            assertionMode = STRICT;
            if (!check.failed) {
                continue;
            }
            pageCheckFailed = true;
            pageableCheckFailed = true;
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
                                                                                     Condition condition) {
        checkList.add(new Check<>(continueMethodName, elementsSupplier, condition));
        return this;
    }

    private class Check<R extends ElementsCollection> {
        private final String checkDescr;
        private boolean failed = false;
        ElementsCollection badElementList;
        private List<String> badElementTextList;
        private final Condition condition;
        private final Function<T, R> getElements;

        private Check(String checkDescr, Function<T, R> getElements, Condition condition) {
            this.checkDescr = checkDescr;
            this.getElements = getElements;
            this.condition = condition;
        }

        private void perform() {
            badElementList = getElements.apply(target).filterBy(not(condition));
            badElementTextList = badElementList.texts();
            failed = !badElementTextList.isEmpty();
        }

        private String passMessage() {
            if (failed)
                return null;
            return "Каждый элемент " + checkDescr;
        }

        private String failedMessage() {
            if (!failed)
                return null;
            return "Элемент (всего таких: " + badElementTextList.size() + ") \"" + badElementTextList.get(0) + "\" не " + checkDescr;
        }
    }
}
