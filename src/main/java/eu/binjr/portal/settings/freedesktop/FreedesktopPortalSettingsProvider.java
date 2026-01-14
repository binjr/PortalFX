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

package eu.binjr.portal.settings.freedesktop;

import eu.binjr.portal.settings.DesktopSettings;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.paint.Color;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;
import java.util.function.BiConsumer;

/**
 * A PlatformPreferences implementation that uses the XDG Desktop portal via DBus to provide
 * the current system-wide appearance settings on Linux.
 * It automatically falls back to the default JavaFX implementation if the interface is not
 * available at runtime.
 */
public class FreedesktopPortalSettingsProvider implements DesktopSettings, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(FreedesktopPortalSettingsProvider.class);
    private static final ColorScheme DEFAULT_COLOR_SCHEME = ColorScheme.LIGHT;
    private static final Color DEFAULT_ACCENT_COLOR = Color.SLATEBLUE;

    private static final String FREEDESKTOP_PORTAL_DESKTOP_BUS_NAME = "org.freedesktop.portal.Desktop";
    private static final String FREEDESKTOP_PORTAL_DESKTOP_PATH = "/org/freedesktop/portal/desktop";
    private static final String FREEDESKTOP_APPEARANCE = "org.freedesktop.appearance";

    private final ReadOnlyObjectProperty<ColorScheme> colorSchemeProperty;
    private final ReadOnlyObjectProperty<Color> accentColorProperty;
    private final ReadOnlyBooleanProperty reducedMotionProperty;
    private final ReadOnlyBooleanProperty highContrastProperty;

    private DBusConnection sessionConnection;

    public FreedesktopPortalSettingsProvider() {
        var colorSchemeWrapper = new ReadOnlyObjectWrapper<>(DEFAULT_COLOR_SCHEME);
        var accentColorWrapper = new ReadOnlyObjectWrapper<>(DEFAULT_ACCENT_COLOR);
        var highContrastWrapper = new ReadOnlyBooleanWrapper(false);
        var reducedMotionWrapper = new ReadOnlyBooleanWrapper(false);
        var foundSettings = EnumSet.noneOf(FreedesktopPortalSettings.class);

        // Define mappings between DBus interface and JavaFX properties
        BiConsumer<String, Variant<?>> settingsMapper = (name, setting) -> {
            switch (FreedesktopPortalSettings.fromName(name)) {
                case COLOR_SCHEME -> {
                    if (setting.getValue() instanceof UInt32 colorScheme) {
                        colorSchemeWrapper.set(switch (colorScheme.intValue()) {
                            // 1: Prefer dark appearance
                            case 1 -> ColorScheme.DARK;
                            // 2: Prefer light appearance
                            case 2 -> ColorScheme.LIGHT;
                            // Unknown values should be treated as 0 (no preference).
                            default -> DEFAULT_COLOR_SCHEME;
                        });
                        foundSettings.add(FreedesktopPortalSettings.COLOR_SCHEME);
                    }
                }
                case ACCENT_COLOR -> {
                    if (setting.getValue() instanceof Double[] color) {
                        accentColorWrapper.set(Color.color(color[0], color[1], color[2]));
                        foundSettings.add(FreedesktopPortalSettings.ACCENT_COLOR);
                    }
                }
                case REDUCED_MOTION -> {
                    if (setting.getValue() instanceof UInt32 contrast) {
                        // 1: Reduced motion
                        reducedMotionWrapper.set(contrast.intValue() == 1);
                        foundSettings.add(FreedesktopPortalSettings.REDUCED_MOTION);
                    }
                }
                case CONTRAST -> {
                    if (setting.getValue() instanceof UInt32 contrast) {
                        // 1: Higher contrast
                        highContrastWrapper.set(contrast.intValue() == 1);
                        foundSettings.add(FreedesktopPortalSettings.CONTRAST);
                    }
                }
            }
        };
        boolean dBusConnectionSuccess = true;
        try {
            // DBus connection must stay alive for the life-time of the app in order to listen to handled signals
            sessionConnection = DBusConnectionBuilder.forSessionBus().build();

            // Read current values for settings exposed by the interface
            var portalSettings = sessionConnection.getRemoteObject(FREEDESKTOP_PORTAL_DESKTOP_BUS_NAME,
                    FREEDESKTOP_PORTAL_DESKTOP_PATH,
                    FreedesktopPortalInterface.class);
            var appearance = portalSettings.ReadAll(new String[]{FREEDESKTOP_APPEARANCE}).get(FREEDESKTOP_APPEARANCE);
            if (appearance != null) {
                appearance.forEach(settingsMapper);
            }

            // Install signal handler to listen to org.freedesktop.portal.Settings::SettingChanged
            FreedesktopPortalInterface changedSettingsSignal =
                    sessionConnection.getRemoteObject(FREEDESKTOP_PORTAL_DESKTOP_BUS_NAME,
                            FREEDESKTOP_PORTAL_DESKTOP_PATH,
                            FreedesktopPortalInterface.class);
            sessionConnection.addSigHandler(FreedesktopPortalInterface.SettingChanged.class, changedSettingsSignal,
                    signal -> {
                        if (signal.getNamespace().equals(FREEDESKTOP_APPEARANCE)) {
                            settingsMapper.accept(signal.getKey(), signal.getValue());
                        }
                    });
        } catch (DBusException e) {
            // Fall back to default JavaFX implementation
            dBusConnectionSuccess = false;
            logger.warn("Failed to retrieve org.freedesktop.portal.Settings interface, " +
                    "falling back to default implementation: " + e.getMessage());
            logger.debug("Call stack", e);
            if (sessionConnection != null) {
                // Attempt to close DBus connection
                try {
                    sessionConnection.close();
                } catch (IOException ex) {
                    logger.warn("Error while attempting to close DBus connection: " + ex.getMessage());
                    logger.debug("Call stack", ex);
                }
            }

        }

        // Assign found settings to output properties, or fall back to JavaFX implementation.
        this.colorSchemeProperty = dBusConnectionSuccess && foundSettings.contains(FreedesktopPortalSettings.COLOR_SCHEME) ?
                colorSchemeWrapper.getReadOnlyProperty() : Platform.getPreferences().colorSchemeProperty();
        this.accentColorProperty = dBusConnectionSuccess && foundSettings.contains(FreedesktopPortalSettings.ACCENT_COLOR) ?
                accentColorWrapper.getReadOnlyProperty() : Platform.getPreferences().accentColorProperty();
        this.reducedMotionProperty = dBusConnectionSuccess && foundSettings.contains(FreedesktopPortalSettings.REDUCED_MOTION) ?
                reducedMotionWrapper.getReadOnlyProperty() : Platform.getPreferences().reducedMotionProperty();
        // There is no JavaFX implementation for a high contrast setting
        this.highContrastProperty = highContrastWrapper.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyObjectProperty<ColorScheme> colorSchemeProperty() {
        return colorSchemeProperty;
    }

    @Override
    public ColorScheme getColorScheme() {
        return colorSchemeProperty.get();
    }

    @Override
    public ReadOnlyObjectProperty<Color> accentColorProperty() {
        return accentColorProperty;
    }

    @Override
    public Color getAccentColor() {
        return accentColorProperty.get();
    }

    @Override
    public ReadOnlyBooleanProperty highContrastProperty() {
        return highContrastProperty;
    }

    @Override
    public boolean isHighContrast() {
        return highContrastProperty.get();
    }

    @Override
    public ReadOnlyBooleanProperty reducedMotionProperty() {
        return reducedMotionProperty;
    }

    @Override
    public boolean isReducedMotion() {
        return reducedMotionProperty.get();
    }

    @Override
    public void close() throws Exception {
        if (sessionConnection != null) {
            sessionConnection.close();
        }
    }
}
