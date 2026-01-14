package eu.binjr.portal;

import eu.binjr.portal.settings.DesktopSettings;
import eu.binjr.portal.settings.javafx.JavaFxSettingsProvider;
import eu.binjr.portal.settings.freedesktop.FreedesktopPortalSettingsProvider;

import java.util.Locale;

public class DesktopPortal {

    private static class SettingsHolder {
        private final static DesktopSettings DESKTOP_SETTINGS =
                System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("linux") ?
                        new FreedesktopPortalSettingsProvider() : new JavaFxSettingsProvider();
    }

    public static DesktopSettings getSettings() {
        return SettingsHolder.DESKTOP_SETTINGS;
    }
}

