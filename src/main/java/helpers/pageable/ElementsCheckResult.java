package helpers.pageable;

import org.opentest4j.MultipleFailuresError;

import java.util.List;
import java.util.Optional;

import static helpers.pageable.StringsUtils.collectionToString;


/**
 * Класс результатов проверки набора элементов, формирует описание проверки ({@code toString()}) и единственную ошибку проверки
 * в случае, если хотя бы один элемент не соответствует условию проверки.
 *
 * @author Achitheus (Yury Yurchenko)
 */
public class ElementsCheckResult {
    private String descriptionPart;
    private final boolean isFailed;
    private int failedElementCount;
    private final AssertionError error;
    private int pageNumber;
    private int checkedElementCount;

    /**
     * Создает объект результатов проверки элементов и формирует ошибку проверки, если проверка провалена.
     * Ошибка проверки всегда одна, даже если количество проваленных элементов больше одного.
     *
     * @param descriptionPart     продолжение фразы "Убедиться, что каждый элемент...", т.е.
     *                            словесное описание элемента, успешно прошедшего проверку.
     *                            Не начинать с отрицания ("не превышает величину X" - плохо).
     *                            Примеры: "содержит подстроку фыва", "соответствует условию: actualPrice > minPrice" и т.п.
     * @param pageNumber          номер страницы, с которой были получены проверенные элементы.
     * @param errorList           список ошибок, каждая из которых получена применением утверждения ({@code Assertion}) к "плохому" элементу.
     * @param checkedElementCount количество проверенных элементов.
     * @author Achitheus (Yury Yurchenko)
     */
    public ElementsCheckResult(String descriptionPart, int pageNumber, List<AssertionError> errorList, int checkedElementCount) {
        init(descriptionPart, errorList.size(), pageNumber, checkedElementCount);
        this.isFailed = !errorList.isEmpty();
        if (errorList.isEmpty()) {
            error = null;
        } else if (errorList.size() == 1) {
            error = errorList.get(0);
        } else {
            error = new MultipleFailuresError(toString(), errorList);
        }
    }

    /**
     * Создает объект результатов проверки элементов и формирует ошибку проверки, если проверка провалена.
     * Ошибка проверки всегда одна, даже если количество проваленных элементов больше одного.
     *
     * @param descriptionPart     продолжение фразы "Убедиться, что каждый элемент...", т.е.
     *                            словесное описание элемента, успешно прошедшего проверку.
     *                            Не начинать с отрицания ("не превышает величину X" - плохо).
     *                            Примеры: "содержит подстроку фыва", "соответствует условию: actualPrice > minPrice" и т.п.
     * @param failedElementList   список элементов проваливших проверку.
     * @param pageNumber          номер страницы, с которой были получены проверенные элементы.
     * @param checkedElementCount количество проверенных элементов.
     * @author Achitheus (Yury Yurchenko)
     */
    public ElementsCheckResult(String descriptionPart, List<?> failedElementList, int pageNumber, int checkedElementCount) {
        init(descriptionPart, failedElementList.size(), pageNumber, checkedElementCount);
        this.isFailed = !failedElementList.isEmpty();
        this.error = isFailed ? new AssertionError(toString() + "\n" + collectionToString(failedElementList)) : null;
    }

    private void init(String descriptionPart, int failedElementCount, int pageNumber, int checkedElementCount) {
        this.descriptionPart = descriptionPart;
        this.failedElementCount = failedElementCount;
        this.pageNumber = pageNumber;
        this.checkedElementCount = checkedElementCount;
    }

    /**
     * Сообщает, провалена ли проверка.
     *
     * @return {@code false}, если проверка прошла успешно (Passed), иначе - {@code true}.
     * @author Achitheus (Yury Yurchenko)
     */
    public boolean isFailed() {
        return isFailed;
    }

    /**
     * Возвращает ошибку проверки элементов, если проверка провалена.
     *
     * @return ошибку проверки элементов.
     * @author Achitheus (Yury Yurchenko)
     */
    public Optional<AssertionError> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public String toString() {
        if (isFailed) {
            return "Стр. " + pageNumber + ". Обнаружено "
                    + failedElementCount + " (из " + checkedElementCount + " шт.) элементов, каждый из которых не " + descriptionPart;
        } else {
            return "Стр. " + pageNumber + ". Каждый элемент ("
                    + checkedElementCount + " шт.) " + descriptionPart;
        }
    }
}
