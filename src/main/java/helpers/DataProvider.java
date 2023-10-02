package helpers;

import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DataProvider {

    public static Stream<Arguments> checkSearchResultsByEnumFilter() {
        String url = "https://ya.ru/";
        String serviceTitle = "Маркет";

        String section1 = "Электроника";
        String category1 = "Смартфоны";
        Map<String, Set<String>> enumFilters1 = new HashMap<>();
        enumFilters1.put("Производитель", Set.of("apple"));

        String section2 = "Ноутбуки и компьютеры";
        String category2 = "Ноутбуки";
        Map<String, Set<String>> enumFilters2 = new HashMap<>();
        enumFilters2.put("Производитель", Set.of("samsung", "ark", "asus", "blackberry"));

        return Stream.of(
                Arguments.of(url, serviceTitle, section1, category1, enumFilters1),
                Arguments.of(url, serviceTitle, section2, category2, enumFilters2)
        );
    }
}
