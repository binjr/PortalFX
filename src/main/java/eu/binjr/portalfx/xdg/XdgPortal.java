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

package eu.binjr.portalfx.xdg;

import eu.binjr.portalfx.settings.XdgSettingsPortal;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Optional;


public abstract class XdgPortal<T extends XdgPortalInterface> {
    private static final Logger logger = LoggerFactory.getLogger(XdgPortal.class);
    public static final String DESKTOP_PORTAL_BUS_NAME = "org.freedesktop.portal.Desktop";
    public static final String DESKTOP_PORTAL_PATH = "/org/freedesktop/portal/desktop";
    public static final int MIN_VERSION = 2;

    private final DBusConnection sessionConnection;
    private final T portalObject;

    protected XdgPortal(Class<T> type) throws DBusException {
        sessionConnection = DBusConnectionBuilder.forSessionBus().withShared(true).build();
        this.portalObject = sessionConnection.getRemoteObject(DESKTOP_PORTAL_BUS_NAME, DESKTOP_PORTAL_PATH, type);
    }

    protected T getPortalObject() {
        return portalObject;
    }

    protected <S extends DBusSignal, I extends XdgPortalInterface>
    void addSignalHandler(Class<S> signalType, DBusSigHandler<S> signalHandler) throws DBusException {
        sessionConnection.addSigHandler(signalType, portalObject, signalHandler);
    }

    public int getVersion() {
        return portalObject.getVersion().intValue();
    }

    public static <T extends XdgPortal<?>> Optional<T> of(Class<T> portalClass) {
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("linux")) {
            try {
                var portal = portalClass.getDeclaredConstructor().newInstance();
                if (portal.getVersion() >= MIN_VERSION) {
                    return Optional.of(portal);
                } else {
                    logger.warn("dBus interface found but version is not supported (found=" +
                            portal.getVersion() + " < minimum=" + MIN_VERSION + ")");
                }
            } catch (DBusExecutionException dBex) {
                logger.error("Failed to establish connection to bus name " + DESKTOP_PORTAL_BUS_NAME +
                        ", interface " + portalClass.getName());
                logger.debug("Stack trace", dBex);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                logger.error("Failed to create new instance of " + portalClass.getName());
                logger.debug("Stack trace", e);
            }

        }
        return Optional.empty();
    }

}
