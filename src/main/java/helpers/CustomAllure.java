package helpers;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

public class CustomAllure {
    public static final Logger log = LoggerFactory.getLogger(CustomAllure.class);
    private static final AllureLifecycle lifecycle = getLifecycle();

    public static void logCurrentStep() {
        Optional<String> currentTestCaseOrStep = lifecycle.getCurrentTestCaseOrStep();
        log.debug("Step UUID: {}", currentTestCaseOrStep.orElse("empty"));
        lifecycle.updateStep(step -> log.debug("Step name: {}", step.getName()));
    }

    /**
     * Run provided {@link Allure.ThrowableRunnable} as step with given name. Takes no effect
     * if no test run at the moment.
     *
     * @param runnable the step's body.
     */
    public static <T> T stepWithChangeableStatus(String name, final Allure.ThrowableRunnable<T> runnable) {
        final String uuid = UUID.randomUUID().toString();
        getLifecycle().startStep(uuid, new StepResult().setName(name));

        try {
            T result = runnable.run();
            getLifecycle().updateStep(uuid, step -> step.setStatus(
                    step.getStatus() == null
                            ? Status.PASSED
                            : step.getStatus()
            ));
            return result;
        } catch (Throwable throwable) {
            getLifecycle().updateStep(s -> s
                    .setStatus(getStatus(throwable).orElse(Status.BROKEN))
                    .setStatusDetails(getStatusDetails(throwable).orElse(null)));
            throw new RuntimeException(throwable);
        } finally {
            getLifecycle().stopStep(uuid);
        }
    }

    public static void stepWithChangeableStatus(String name, final Allure.ThrowableRunnableVoid runnable) {
        final String uuid = UUID.randomUUID().toString();
        getLifecycle().startStep(uuid, new StepResult().setName(name));

        try {
            runnable.run();
            getLifecycle().updateStep(uuid, step -> step.setStatus(
                    step.getStatus() == null
                            ? Status.PASSED
                            : step.getStatus()
            ));
        } catch (Throwable throwable) {
            getLifecycle().updateStep(s -> s
                    .setStatus(getStatus(throwable).orElse(Status.BROKEN))
                    .setStatusDetails(getStatusDetails(throwable).orElse(null)));
            ExceptionUtils.sneakyThrow(throwable);
        } finally {
            getLifecycle().stopStep(uuid);
        }
    }

}
