package helpers.selenide;

import com.codeborne.selenide.conditions.TextCondition;
import com.codeborne.selenide.impl.Html;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class OneOfTexts extends TextCondition {
    Collection<String> targets;

    public OneOfTexts(Collection<String> targets) {
        super("one of texts", targets.toString());
        if (targets.stream().anyMatch(target -> Objects.isNull(target) || target.isBlank())) {
            throw new IllegalArgumentException("The collection must not contain null or blank strings");
        }
        this.targets = targets;
    }

    @CheckReturnValue
    @Override
    protected boolean match(String actualText, String expectedText) {
        return targets.stream()
                .anyMatch(target -> Html.text.contains(actualText, target));
    }
}