package eu.binjr.portalfx;

import eu.binjr.portalfx.documents.DocumentsPortal;
import eu.binjr.portalfx.settings.SettingsPortal;
import eu.binjr.portalfx.settings.JavaFxSettingsPortal;
import eu.binjr.portalfx.settings.XdgSettingsPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class Portal {
    private static final Logger logger = LoggerFactory.getLogger(Portal.class);

    private static class SettingsHolder {
        private final static SettingsPortal SETTINGS_PORTAL = portalFactory(XdgSettingsPortal.class, JavaFxSettingsPortal.class);
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

    private static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("linux");
    }

    private static <T, U extends T, V extends T> T portalFactory(Class<U> portalClass, Class<V> fallback) {

        if (isLinux()) {
            try {
                return fallback.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                logger.error("Failed to create new instance of {}", portalClass.getName());
                logger.debug("Stack trace", e);
            }
        }
        try {
            return portalClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to create new instance of " + portalClass.getName(), e);
        }


    }
}

