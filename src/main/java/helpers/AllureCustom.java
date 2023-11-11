package helpers;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;

import java.util.function.Consumer;

import static io.qameta.allure.Allure.getLifecycle;

public class AllureCustom {
    public static void markOuterStepAsFailedAndStop() {
        AllureLifecycle lifecycle = getLifecycle();
        lifecycle.updateStep(step -> step.setStatus(Status.FAILED));
        lifecycle.stopStep();
    }

    public static void stepSoftAssert(final String passMessage, final String failMessage,
                                      final Consumer<String> messagedAssert, final boolean markStepAsFailed) {
        Allure.step(markStepAsFailed ? failMessage : passMessage
                , () -> {
                    messagedAssert.accept(failMessage);
                    if (markStepAsFailed) {
                        markOuterStepAsFailedAndStop();
                    }
                });
    }

}
