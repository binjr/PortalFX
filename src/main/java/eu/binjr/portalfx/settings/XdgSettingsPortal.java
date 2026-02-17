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

package eu.binjr.portalfx.settings;

import eu.binjr.portalfx.xdg.XdgPortal;
import javafx.application.ColorScheme;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.paint.Color;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

/**
 * A PlatformPreferences implementation that uses the XDG Desktop portal via DBus to provide
 * the current system-wide appearance settings on Linux.
 * It automatically falls back to the default JavaFX implementation if the interface is not
 * available at runtime.
 */
public class XdgSettingsPortal extends XdgPortal<XdgSettingsInterface> implements SettingsPortal {

    private static final Logger logger = LoggerFactory.getLogger(XdgSettingsPortal.class);
    private static final ColorScheme DEFAULT_COLOR_SCHEME = ColorScheme.LIGHT;

    private static final String FREEDESKTOP_APPEARANCE = "org.freedesktop.appearance";
    static private final String COLOR_SCHEME = "color-scheme";
    static private final String CONTRAST = "contrast";
    static private final String REDUCED_MOTION = "reduced-motion";
    static private final String ACCENT_COLOR = "accent-color";


    private final ReadOnlyObjectWrapper<ColorScheme> colorSchemeWrapper = new ReadOnlyObjectWrapper<>(DEFAULT_COLOR_SCHEME);
    private final ReadOnlyObjectWrapper<Color> accentColorWrapper = new ReadOnlyObjectWrapper<>(Color.SLATEBLUE);
    private final ReadOnlyBooleanWrapper highContrastWrapper = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper reducedMotionWrapper = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyObjectProperty<ColorScheme> colorSchemeProperty = colorSchemeWrapper.getReadOnlyProperty();
    private final ReadOnlyObjectProperty<Color> accentColorProperty = accentColorWrapper.getReadOnlyProperty();
    private final ReadOnlyBooleanProperty reducedMotionProperty = reducedMotionWrapper.getReadOnlyProperty();
    private final ReadOnlyBooleanProperty highContrastProperty = highContrastWrapper.getReadOnlyProperty();


    public XdgSettingsPortal() throws DBusException {
        super(XdgSettingsInterface.class);
        // Define mappings between DBus interface and JavaFX properties
        BiConsumer<String, Variant<?>> settingsMapper = (name, setting) -> {
            switch (name) {
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
                    }
                }
                case ACCENT_COLOR -> {
                    if (setting.getValue() instanceof Double[] color) {
                        accentColorWrapper.set(Color.color(color[0], color[1], color[2]));
                    }
                }
                case REDUCED_MOTION -> {
                    if (setting.getValue() instanceof UInt32 contrast) {
                        // 1: Reduced motion
                        reducedMotionWrapper.set(contrast.intValue() == 1);
                    }
                }
                case CONTRAST -> {
                    if (setting.getValue() instanceof UInt32 contrast) {
                        // 1: Higher contrast
                        highContrastWrapper.set(contrast.intValue() == 1);
                    }
                }
            }
        };

        // Read current values for settings exposed by the interface
        var appearance = this.getPortalObject().ReadAll(new String[]{FREEDESKTOP_APPEARANCE}).get(FREEDESKTOP_APPEARANCE);
        if (appearance != null) {
            appearance.forEach(settingsMapper);
        }

        // Install signal handler to listen to org.freedesktop.portal.Settings::SettingChanged
        this.addSignalHandler(XdgSettingsInterface.SettingChanged.class,
                signal -> {
                    if (signal.getNamespace().equals(FREEDESKTOP_APPEARANCE)) {
                        settingsMapper.accept(signal.getKey(), signal.getValue());
                    }
                });
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

}
