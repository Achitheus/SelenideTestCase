package helpers;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DataProvider {
    /**
     * Предоставляет данные для параметризованного теста.
     *
     * @return Поток наборов аргументов.
     * @author Юрий Юрченко
     */
    public static Stream<Arguments> checkSearchResultsByEnumFilter() {
        String url = "https://ya.ru/";
        String serviceTitle = "Маркет";
        String section = "Электроника";
        String category = "Смартфоны";
        Map<String, Set<String>> enumFilters1 = Map.of("Производитель", Set.of("ASUS"));
        Map<String, Set<String>> enumFilters2 = Map.of("Производитель", Set.of("Black Shark"));
        Map<String, Set<String>> enumFilters3 = Map.of("Производитель", Set.of("OnePlus"));
        Map<String, Set<String>> enumFilters4 = Map.of("Производитель", Set.of("Apple"));
        Map<String, Set<String>> enumFilters5 = Map.of("Производитель", Set.of("Google"));
        Map<String, Set<String>> enumFilters6 = Map.of("Производитель", Set.of("Seals"));

        Map<String, Set<String>> enumCheckSets1 = Map.of("Производитель", enumFilters1.get("Производитель"));
        Map<String, Set<String>> enumCheckSets2 = Map.of("Производитель", enumFilters2.get("Производитель"));
        Map<String, Set<String>> enumCheckSets3 = Map.of("Производитель", Set.of("OnePlus", "One Plus"));
        Map<String, Set<String>> enumCheckSets4 = Map.of("Производитель", Set.of("Apple", "IPhone"));
        Map<String, Set<String>> enumCheckSets5 = Map.of("Производитель", enumFilters5.get("Производитель"));
        Map<String, Set<String>> enumCheckSets6 = Map.of("Производитель", enumFilters6.get("Производитель"));
        return Stream.of(
                Arguments.of(url, serviceTitle, section, category, enumFilters1, enumCheckSets1),
                Arguments.of(url, serviceTitle, section, category, enumFilters2, enumCheckSets2),
                Arguments.of(url, serviceTitle, section, category, enumFilters3, enumCheckSets3),
                Arguments.of(url, serviceTitle, section, category, enumFilters4, enumCheckSets4),
                Arguments.of(url, serviceTitle, section, category, enumFilters5, enumCheckSets5),
                Arguments.of(url, serviceTitle, section, category, enumFilters6, enumCheckSets6)
        );
    }
}
