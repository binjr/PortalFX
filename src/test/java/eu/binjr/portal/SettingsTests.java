package eu.binjr.portal;

import javafx.application.ColorScheme;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SettingsTests {

    private static final long TIMEOUT = 5000;

    @BeforeAll
    public static void initToolkit() {
        Platform.startup(() -> {
        });
    }

    @Test
    void coloScheme() throws Exception {
        assertNotNull(runOnFxThread((() -> DesktopPortal.getSettings().colorSchemeProperty())));
    }

    @Test
    void accentColor() throws Exception {
        assertNotNull(runOnFxThread((() -> DesktopPortal.getSettings().accentColorProperty())));
    }

    @Test
    void highContrast() throws Exception {
        assertNotNull(runOnFxThread((() -> DesktopPortal.getSettings().highContrastProperty())));
    }

    @Test
    void reduceMotion() throws Exception {
        assertNotNull(runOnFxThread((() -> DesktopPortal.getSettings().reducedMotionProperty())));
    }

    <T> T runOnFxThread(Supplier<T> supplier) throws InterruptedException, TimeoutException {
        var latch = new CountDownLatch(1);
        List<T> res = new ArrayList<>(1);
        Platform.runLater(() -> {
            res.add(supplier.get());
            latch.countDown();
        });
        if (latch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
            return res.getFirst();
        } else {
            throw new TimeoutException("Timeout while running supplier");
        }
    }
}
