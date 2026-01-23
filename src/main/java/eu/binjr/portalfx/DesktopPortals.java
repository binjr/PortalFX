package eu.binjr.portalfx;

import eu.binjr.portalfx.documents.DocumentsPortal;
import eu.binjr.portalfx.settings.SettingsPortal;
import eu.binjr.portalfx.settings.javafx.JavaFxSettingsPortal;
import eu.binjr.portalfx.settings.xdg.XdgSettingsPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class DesktopPortals {
    private static final Logger logger = LoggerFactory.getLogger(DesktopPortals.class);

    private static class SettingsHolder {
        private final static SettingsPortal SETTINGS_PORTAL =
                System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("linux") ?
                        new XdgSettingsPortal() : new JavaFxSettingsPortal();
    }

    private static class DocumentsHolder {
        private final static DocumentsPortal DOCUMENT_PORTAL = new DocumentsPortal();
//                System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("linux") ?

    }

    public static SettingsPortal settings() {
        return SettingsHolder.SETTINGS_PORTAL;
    }

    public static DocumentsPortal documents() {
        return DocumentsHolder.DOCUMENT_PORTAL;
    }

}

