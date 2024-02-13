package helpers.pageable;

import java.util.Collection;
import java.util.Iterator;

public class StringsUtils {
    /**
     * Преобразует коллекцию в строку, разделяя выводимые элементы переходом на новую строку.
     *
     * @param collection коллекция.
     * @return строковое представление коллекции.
     * @author Achitheus (Yury Yurchenko)
     */
    public static String collectionToString(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        if (!it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        while (true) {
            Object e = it.next();
            sb.append(e);
            if (!it.hasNext())
                return sb.append(']').toString();
            sb.append('\n');
        }
    }
}
