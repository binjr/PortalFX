package eu.binjr.portalfx;

import eu.binjr.portalfx.settings.DesktopSettings;
import eu.binjr.portalfx.settings.javafx.JavaFxSettingsProvider;
import eu.binjr.portalfx.settings.freedesktop.FreedesktopSettingsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Locale;

public class Portal {

    private static final String DOCUMENT_PORTAL_HOST_PATH = "document-portal.host-path";
    private static final Logger logger = LoggerFactory.getLogger(Portal.class);

    private static class SettingsHolder {
        private final static DesktopSettings DESKTOP_SETTINGS =
                System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("linux") ?
                        new FreedesktopSettingsProvider() : new JavaFxSettingsProvider();
    }

    public static DesktopSettings getSettings() {
        return SettingsHolder.DESKTOP_SETTINGS;
    }


    public static Path toHostFsPath(Path sandboxPath) {
        if (Files.exists(sandboxPath)) {
            var userDefView = Files.getFileAttributeView(sandboxPath, UserDefinedFileAttributeView.class);
            try {
                if (userDefView.list().contains(DOCUMENT_PORTAL_HOST_PATH)) {
                    ByteBuffer attrBuffer = ByteBuffer.allocate(userDefView.size(DOCUMENT_PORTAL_HOST_PATH));
                    userDefView.read(DOCUMENT_PORTAL_HOST_PATH, attrBuffer);
                    attrBuffer.flip();
                    return Path.of(Charset.defaultCharset().decode(attrBuffer).toString());
                }
            } catch (IOException e) {
                logger.debug("Error trying to retrieve host path", e);
            }
        }
        return sandboxPath;
    }
}

