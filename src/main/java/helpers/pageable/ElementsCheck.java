package helpers.pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Базовый абстрактный класс проверки элементов.
 *
 * @param <T> тип объекта, предоставляющего набор проверяемых элементов (page object).
 * @author Achitheus (Yury Yurchenko)
 */
public abstract class ElementsCheck<T> implements ElementsCheckWithErrorCollector<T> {
    protected String passedElementDescription;
    protected final List<AssertionError> collectedErrors = new ArrayList<>();
    protected int checkNumber = 0;

    public ElementsCheck(String passedElementDescription) {
        this.passedElementDescription = passedElementDescription;
    }

    @Override
    final public ElementsCheckResult perform(T target) {
        checkNumber++;
        return performWithoutNumberIncrement(target);
    }

    protected abstract ElementsCheckResult performWithoutNumberIncrement(T target);

    @Override
    final public Collection<? extends AssertionError> getCollectedErrors() {
        return collectedErrors;
    }
}
