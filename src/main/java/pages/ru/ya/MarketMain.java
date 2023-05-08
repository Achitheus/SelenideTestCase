package pages.ru.ya;

public class MarketMain {

    private MarketMain openCatalog() {
        return this;
    }

    private MarketMain navigateToCategory(String category) {
        return this;
    }

    public <T extends BasePage> T toCategorySection(String category, String section, Class<T> nextPage) {
        openCatalog().navigateToCategory(category).goToSection(section);

        return null;
    }

    private BasePage goToSection(String section) {
        return null;
    }
}
