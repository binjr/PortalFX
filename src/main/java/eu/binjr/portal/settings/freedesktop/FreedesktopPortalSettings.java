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

import java.util.Arrays;

/**
 * A list of standardized host settings exposed via xdg-desktop-portal Settings interface.
 * See specifications at:
 * https://flatpak.github.io/xdg-desktop-portal/docs/doc-org.freedesktop.portal.Settings.html#description
 *
 */
public enum FreedesktopPortalSettings {
    COLOR_SCHEME("color-scheme"),
    CONTRAST("contrast"),
    REDUCED_MOTION("reduced-motion"),
    ACCENT_COLOR("accent-color"),
    UNKNOWN("unknown");

    private final String name;

    FreedesktopPortalSettings(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static FreedesktopPortalSettings fromName(String name) {
        return Arrays.stream(FreedesktopPortalSettings.values())
                .filter(e -> e.getName().equals(name))
                .findAny()
                .orElse(UNKNOWN);
    }
}
