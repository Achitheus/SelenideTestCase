package helpers.pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Класс для проверки элементов страницы с помощью переданного <i>утверждения</i> ({@code assertion}).
 * При каждом вызове perform() сохраняет ошибку, если хотя бы один элемент не прошел поверку.
 * Список ошибок можно получить вызвав {@link  AssertionCheckThatEachElement#getCollectedErrors()}.
 * Рекомендуется использовать только в контексте класса {@link PageableChecker}.
 *
 * @param <PAGE_OBJ> класс объекта, предоставляющего набор проверяемых элементов
 * @param <E>        тип проверяемых элементов
 * @author Achitheus (Yury Yurchenko)
 */
public class AssertionCheckThatEachElement<PAGE_OBJ, E> extends ElementsCheck<PAGE_OBJ> {
    private final Function<PAGE_OBJ, Collection<E>> elementsProvider;
    private final BiConsumer<E, String> assertion;

    /**
     * Создает новый объект проверки.
     *
     * @param continueConstructorName продолжение фразы "Убедиться, что каждый элемент...", т.е.
     *                                словесное описание элемента, соответствующего утверждению {@code assertion}.
     *                                Не начинать с отрицания ("не превышает величину X" - плохо).
     *                                Примеры: "содержит подстроку фыва", "соответствует условию: actualPrice > minPrice" и т.п.
     * @param elementsProvider        функция, предоставляющая коллекцию проверяемых объектов.
     * @param assertion               утверждение о проверяемом элементе, где первый параметр сам элемент,
     *                                второй - сформированное сообщение. Пример, если элемент является ценой: <p>
     *                                {@code (price, message) -> assertTrue(price < 10_000, message)}.
     * @author Achitheus (Yury Yurchenko)
     */
    public AssertionCheckThatEachElement(String continueConstructorName, Function<PAGE_OBJ, Collection<E>> elementsProvider, BiConsumer<E, String> assertion) {
        super(continueConstructorName);
        this.assertion = assertion;
        this.elementsProvider = elementsProvider;
    }

    @Override
    protected ElementsCheckResult performWithoutNumberIncrement(PAGE_OBJ target) {
        Collection<E> elementCollection = elementsProvider.apply(target);
        List<AssertionError> errorList = new ArrayList<>();
        for (E el : elementCollection) {
            try {
                assertion.accept(el, "Элемент \"" + el + "\" не " + passedElementDescription);
            } catch (AssertionError error) {
                errorList.add(error);
            }
        }

        ElementsCheckResult elementsCheckResult = new ElementsCheckResult(passedElementDescription, checkNumber, errorList, elementCollection.size());
        elementsCheckResult.getError().ifPresent(collectedErrors::add);
        return elementsCheckResult;
    }
}
