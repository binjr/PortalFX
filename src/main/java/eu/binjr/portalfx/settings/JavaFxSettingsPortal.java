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

import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.paint.Color;

/**
 * A DesktopPortal implementation that acts as a passthrough to the default JavaFX implementation
 */
public class JavaFxSettingsPortal implements SettingsPortal {
    private final ReadOnlyObjectProperty<ColorScheme> colorSchemeProperty = Platform.getPreferences().colorSchemeProperty();
    private final ReadOnlyObjectProperty<Color> accentColorProperty = Platform.getPreferences().accentColorProperty();
    private final ReadOnlyBooleanProperty reducedMotionProperty = Platform.getPreferences().reducedMotionProperty();
    // There is no JavaFX implementation for a high contrast setting
    private final ReadOnlyBooleanProperty highContrastProperty = new ReadOnlyBooleanWrapper(false).getReadOnlyProperty();

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
