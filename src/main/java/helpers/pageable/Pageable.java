package helpers.pageable;

/**
 * Имплементация данного интерфейса классом позволяет использовать {@link PageableChecker} для его объектов.
 *
 * @author Achitheus (Yury Yurchenko)
 */
public interface Pageable {
    /**
     * Осуществляет переход на следующую страницу, если это возможно.
     *
     * @return {@code true}, если переход успешно выполнен, иначе - {@code false}.
     * @author Achitheus (Yury Yurchenko)
     */
    boolean nextPage();
}
