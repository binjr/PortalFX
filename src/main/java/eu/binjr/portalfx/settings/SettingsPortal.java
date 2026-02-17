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
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.paint.Color;

public interface SettingsPortal {
    
    /**
     * The platform color scheme, which specifies whether applications should prefer light text on
     * dark backgrounds, or dark text on light backgrounds.
     *
     * @return the {@code colorScheme} property
     */
    ReadOnlyObjectProperty<ColorScheme> colorSchemeProperty();

    /**
     * Returns the current {@code colorScheme}.
     *
     * @return the current {@code colorScheme}
     */
    ColorScheme getColorScheme();

    /**
     * The accent color, which can be used to highlight the active or important part of a
     * control and make it stand out from the rest of the user interface. It is usually a
     * vivid color that contrasts with the foreground and background colors.
     *
     * @return the {@code accentColor} property
     */
    ReadOnlyObjectProperty<Color> accentColorProperty();

    /**
     * Returns the current {@code accentColor}.
     *
     * @return the current {@code accentColor}
     */
    Color getAccentColor();

    ReadOnlyBooleanProperty highContrastProperty();

    /**
     * Returns {@code true} if display is set to high contrast, {@code false} otherwise.
     *
     * @return {@code true} if display is set to high contrast, {@code false} otherwise
     */
    boolean isHighContrast();

    /**
     * Specifies whether applications should minimize the amount of non-essential animations,
     * reducing discomfort for users who experience motion sickness or vertigo.
     *
     * @return the {@code reducedMotion} property
     */
    ReadOnlyBooleanProperty reducedMotionProperty();

    /**
     * Returns {@code true} if applications should minimize the amount of animations, {@code false} otherwise.
     *
     * @return {@code true} if applications should minimize the amount of animations, {@code false} otherwise.
     */
    boolean isReducedMotion();


}
