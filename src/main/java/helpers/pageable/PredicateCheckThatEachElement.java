package helpers.pageable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Класс для проверки элементов, получаемых с помощью функции {@code elementsProvider}, на соответствие <i>предикату</i> {@code condition}.
 * При каждом вызове perform() сохраняет ошибку, если хотя бы один элемент не прошел поверку. Список ошибок
 * можно получить вызвав {@link PredicateCheckThatEachElement#getCollectedErrors()}. Рекомендуется использовать
 * только в контексте класса {@link PageableChecker}.
 *
 * @param <PAGE_OBJ> объект, предоставляющий набор проверяемых элементов
 * @param <E>        проверяемый элемент
 * @author Achitheus (Yury Yurchenko)
 */
public class PredicateCheckThatEachElement<PAGE_OBJ, E> extends ElementsCheck<PAGE_OBJ> {
    private final Predicate<E> condition;
    private final Function<PAGE_OBJ, Collection<E>> elementsProvider;

    /**
     * Создает новый объект проверки.
     *
     * @param continueConstructorName продолжение фразы "Убедиться, что каждый элемент...", т.е.
     *                                словесное описание элемента, соответствующего условию {@code condition}.
     *                                Не начинать с отрицания ("не превышает величину X" - плохо).
     *                                Примеры: "содержит подстроку фыва", "соответствует условию: actualPrice > minPrice" и т.п.
     * @param elementsProvider        функция, предоставляющая коллекцию проверяемых объектов.
     * @param condition               условие, которым проверяется каждый элемент. Элемент прошел проверку, если данное условие вернуло {@code true}.
     * @author Achitheus (Yury Yurchenko)
     */
    public PredicateCheckThatEachElement(String continueConstructorName, Function<PAGE_OBJ, Collection<E>> elementsProvider, Predicate<E> condition) {
        super(continueConstructorName);
        this.condition = condition;
        this.elementsProvider = elementsProvider;
    }

    /**
     * @author Achitheus (Yury Yurchenko)
     */
    @Override
    protected ElementsCheckResult performWithoutNumberIncrement(PAGE_OBJ target) {
        Collection<E> elementCollection = elementsProvider.apply(target);
        List<E> failedElementList = elementCollection.stream()
                .filter(condition.negate())
                .collect(Collectors.toList());
        ElementsCheckResult elementsCheckResult = new ElementsCheckResult(passedElementDescription, failedElementList, checkNumber, elementCollection.size());
        elementsCheckResult.getError().ifPresent(collectedErrors::add);
        return elementsCheckResult;
    }
}
