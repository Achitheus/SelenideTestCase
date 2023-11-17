package helpers.selenide;

import com.codeborne.selenide.conditions.TextCondition;

import java.util.Collection;

public class AnyText extends TextCondition {
    Collection<String> targets;

    public AnyText(Collection<String> targets) {
        super("any text", targets.toString());
        this.targets = targets;
    }

    @Override
    protected boolean match(String actualText, String expectedText) {
        return targets.stream()
                .anyMatch(target -> actualText.toLowerCase().contains(target.toLowerCase()));
    }
}