package helpers;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;

import static io.qameta.allure.Allure.getLifecycle;

public class AllureCustom {
    public static final Logger log = LoggerFactory.getLogger(AllureCustom.class);
    private static final AllureLifecycle lifecycle = getLifecycle();

    public static void logCurrentStep() {
        Optional<String> currentTestCaseOrStep = lifecycle.getCurrentTestCaseOrStep();
        log.debug("Step UUID: {}", currentTestCaseOrStep.orElse("empty"));
        lifecycle.updateStep(step -> log.debug("Step name: {}", step.getName()));
    }

    public static void logTestCaseUUID() {
        lifecycle.updateTestCase(testResult -> log.debug("Current test-case UUID: {}", testResult.getUuid()));
    }

    public static void markOuterStepAsFailedAndStop() {
        log.debug("Gonna fail&kill step: ");
        logCurrentStep();
        lifecycle.updateStep(step -> step.setStatus(Status.FAILED));
        lifecycle.stopStep();
    }

    public static void stepSoftAssert(final String passMessage, final String failMessage,
                                      final Consumer<String> messagedAssert, final boolean markStepAsFailed) {
        Allure.step(markStepAsFailed ? failMessage : passMessage
                , () -> {
                    if (markStepAsFailed) {
                        messagedAssert.accept(failMessage);
                        markOuterStepAsFailedAndStop();
                    }
                });
    }

}
