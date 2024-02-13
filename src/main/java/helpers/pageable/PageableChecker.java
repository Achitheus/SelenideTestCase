package helpers.pageable;

import helpers.CustomAllure;
import io.qameta.allure.model.Status;
import org.openqa.selenium.WebDriver;
import org.opentest4j.MultipleFailuresError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static helpers.CustomAllure.stepWithChangeableStatus;
import static io.qameta.allure.Allure.addAttachment;
import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

/**
 * Класс для выполнения нескольких (или одной) проверок элементов страницы за один прогон по страницам.
 * Используемые в {@code javadoc} этого пакета определения: <p>
 * <i>Чек</i> - "подпроверка", например наличие картинки у элементов или корректности формата описания элементов. <p>
 * <i>Проверка</i> - общая совокупная проверка, запускаемая методом {@code run()}, и включающая один или множество <i>чеков</i>. <p>
 * Проверка поддерживает два режима: <i>lazy</i> и <i>eager</i>. <i>Lazy</i> - чек, однажды провалившись, больше не выполняется.
 * <i>Eager</i> - проверяются все чеки на всех проверяемых страницах.
 *
 * @param <PAGE_OBJ> тип объекта, предоставляющего набор проверяемых элементов.
 * @author Achitheus (Yury Yurchenko)
 */
public class PageableChecker<PAGE_OBJ extends Pageable> {
    private final WebDriver driver;
    private final List<AssertionError> errorList = new ArrayList<>();
    private boolean pageableCheckFailed;
    private boolean lazyMode = true;
    private final PAGE_OBJ target;
    private final List<ElementsCheckWithErrorCollector<PAGE_OBJ>> checkList;
    private int pageCount = 1_000;

    /**
     * Создает объект проверки наборов элементов страниц одним или множеством чеков.
     *
     * @param target целевой объект, содержащий методы, которые возвращают коллекцию проверяемых элементов (page object).
     * @param driver WebDriver для сохранения page source там, где был провален хотя бы один чек.
     * @author Achitheus (Yury Yurchenko)
     */
    public PageableChecker(PAGE_OBJ target, WebDriver driver) {
        this.driver = driver;
        this.target = target;
        checkList = new ArrayList<>();
    }

    /**
     * Переключает режимы <i>Eager/Lazy</i>, где <i>lazy</i> - режим, установленный по дефолту, при котором проваленный единожды чек на последующих
     * страницах пропускается, а проверка заканчивается, если не проваленных чеков не осталось, <i>eager</i> - выполняются все
     * чеки на всех проверяемых страницах.
     *
     * @param value если {@code true}, режим устанавливается <i>lazy</i>, иначе - режим <i>eager</i>.
     * @return текущий объект проверки страниц ({@code this}).
     * @author Achitheus (Yury Yurchenko)
     */
    public PageableChecker<PAGE_OBJ> beLazy(boolean value) {
        this.lazyMode = value;
        return this;
    }

    /**
     * Устанавливает количество страниц, которые будут проверены при запуске. По дефолту это значение равно {@code pageCount}
     * для исключения возможности бесконечного цикла.
     *
     * @param pageCount количество страниц, которые будут проверены при запуске.
     * @return текущий объект проверки страниц ({@code this}).
     * @author Achitheus (Yury Yurchenko)
     */
    public PageableChecker<PAGE_OBJ> setPageCount(int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    /**
     * Выполняет добавленные чеки на проверяемых страницах и по завершению выбрасывает все собранные
     * в чеках ошибки в виде {@code MultipleFailuresError}.
     *
     * @throws RuntimeException если список чеков пуст.
     * @return объект, предоставляющий наборы проверяемых элементов (page object).
     * @author Achitheus (Yury Yurchenko)
     */
    public PAGE_OBJ run() {
        runWithoutThrowing();
        assertAll();
        return target;
    }

    /**
     * Выбрасывает все накопленные в чеках ошибки ({@code AssertionError}).
     *
     * @author Achitheus (Yury Yurchenko)
     */
    public void assertAll() {
        checkList.forEach(check -> errorList.addAll(check.getCollectedErrors()));
        if (!errorList.isEmpty())
            throw new MultipleFailuresError("Pageable check assertion failures:", errorList);
    }

    /**
     * Добавляет к проверке предоставленный чек, который будет выполняться на страницах при вызове {@code run()}.
     *
     * @param check добавляемая в общий список проверка элементов страницы (чек).
     * @return текущий объект проверки страниц ({@code this}).
     * @author Achitheus (Yury Yurchenko)
     */
    public PageableChecker<PAGE_OBJ> addCheck(ElementsCheckWithErrorCollector<PAGE_OBJ> check) {
        checkList.add(check);
        return this;
    }

    /**
     * Делает то же, что и {@link PageableChecker#run()}, но не выбрасывает накопленные в чеках ошибки.
     * Позволяет отложить выброс ошибок на более поздний этап. При использовании не забывать вызвать в конце
     * {@link PageableChecker#assertAll()}!
     *
     * @return текущий объект проверки страниц ({@code this}).
     * @author Achitheus (Yury Yurchenko)
     */
    public PageableChecker<PAGE_OBJ> runWithoutThrowing() {
        stepWithChangeableStatus("Постраничная проверка. Режим " + (lazyMode
                        ? "lazy (упавший чек на последующих стр. пропускается)"
                        : "eager (чеки не скипаются, проверяются все страницы)"),
                () -> {
                    if (checkList.isEmpty()) {
                        throw new RuntimeException("Checklist is empty");
                    }
                    List<ElementsCheckWithErrorCollector<PAGE_OBJ>> activeChecks = new ArrayList<>(checkList);
                    int currentPageNumber = 0;
                    do {
                        currentPageNumber++;
                        stepWithChangeableStatus("Страница " + currentPageNumber + (lazyMode ? ". Активных проверок: " + activeChecks.size() : ""),
                                () -> {
                                    if (!processPageCheck(activeChecks)) {
                                        pageableCheckFailed = true;
                                        getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
                                        addAttachment("Page source", "text/html", driver.getPageSource(), ".html");
                                    }
                                }
                        );
                    } while (!activeChecks.isEmpty() && currentPageNumber < pageCount && target.nextPage());
                    if (pageableCheckFailed) {
                        getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
                    }
                }
        );
        return this;
    }

    /**
     * @author Achitheus (Yury Yurchenko)
     */
    private boolean processPageCheck(List<ElementsCheckWithErrorCollector<PAGE_OBJ>> mutableCheckList) {
        boolean pagePassed = true;
        ListIterator<ElementsCheckWithErrorCollector<PAGE_OBJ>> checksIter = mutableCheckList.listIterator();
        while (checksIter.hasNext()) {
            ElementsCheckWithErrorCollector<PAGE_OBJ> check = checksIter.next();
            ElementsCheckResult elementsCheckResult = CustomAllure.stepWithChangeableStatus("step", () -> {
                ElementsCheckResult checkResultInner = check.perform(target);
                getLifecycle().updateStep(step -> step.setName(checkResultInner.toString()));

                if (checkResultInner.isFailed()) {
                    getLifecycle().updateStep(s -> s
                            .setStatus(Status.FAILED)
                            .setStatusDetails(getStatusDetails(checkResultInner.getError().orElse(null)).orElse(null))
                    );
                }
                return checkResultInner;
            });
            if (elementsCheckResult.isFailed()) {
                pagePassed = false;
                if (lazyMode) {
                    checksIter.remove();
                }
            }
        }
        return pagePassed;
    }

}
