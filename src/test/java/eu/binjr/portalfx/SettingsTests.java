/*
 * Copyright 2026 Frederic Thevenet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.binjr.portalfx;

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
        assertNotNull(runOnFxThread((() -> Portal.settings().colorSchemeProperty())));
    }

    @Test
    void accentColor() throws Exception {
        assertNotNull(runOnFxThread((() -> Portal.settings().accentColorProperty())));
    }

    @Test
    void highContrast() throws Exception {
        assertNotNull(runOnFxThread((() -> Portal.settings().highContrastProperty())));
    }

    @Test
    void reduceMotion() throws Exception {
        assertNotNull(runOnFxThread((() -> Portal.settings().reducedMotionProperty())));
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
