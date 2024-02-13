package helpers.pageable;

import java.util.Collection;

/**
 * Интерфейс, реализация которого позволяет добавлять объекты класса в {@link PageableChecker} в качестве объекта
 * проверяющего набор элементов (чек), однако рекомендуется не реализовывать его напрямую, а наследоваться от {@link ElementsCheck}.
 *
 * @param <T> тип объекта, предоставляющего набор проверяемых элементов
 * @author Achitheus (Yury Yurchenko)
 */
public interface ElementsCheckWithErrorCollector<T> {
    /**
     * Производит проверку элементов, предоставленных объектом {@code target} и
     * сохраняет объект ошибки, если хотя бы один элемент провалил проверку.
     *
     * @param target объект, предоставляющий набор проверяемых элементов (page object).
     * @return результат произведенной проверки.
     * @author Achitheus (Yury Yurchenko)
     */
    ElementsCheckResult perform(T target);

    /**
     * Возвращает все накопленные при вызовах {@code perform()} ошибки проверок элементов.
     *
     * @return список ошибок проверки элементов.
     * @author Achitheus (Yury Yurchenko)
     */
    Collection<? extends AssertionError> getCollectedErrors();
}
