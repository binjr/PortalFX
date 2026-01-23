package eu.binjr.portalfx.xdg;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;


public abstract class AbstractDesktopPortal {
    public static final String DESKTOP_PORTAL_BUS_NAME = "org.freedesktop.portal.Desktop";
    public static final String DESKTOP_PORTAL_PATH = "/org/freedesktop/portal/desktop";

    private final DBusConnection sessionConnection;

    protected AbstractDesktopPortal() throws DBusException {
        sessionConnection = DBusConnectionBuilder.forSessionBus().withShared(true).build();
    }

    protected <T extends XdgPortalInterface> T getPortalObject(Class<T> interfaceType) throws DBusException {
        return sessionConnection.getRemoteObject(DESKTOP_PORTAL_BUS_NAME, DESKTOP_PORTAL_PATH, interfaceType);
    }

    protected <T extends DBusSignal, I extends XdgPortalInterface> void addSignalHandler(Class<T> signalType, Class<I> interfaceType, DBusSigHandler<T> signalHandler) throws DBusException {
        addSignalHandler(signalType, getPortalObject(interfaceType), signalHandler);
    }

    protected <T extends DBusSignal> void addSignalHandler(Class<T> signalType, DBusInterface dbusObject, DBusSigHandler<T> signalHandler) throws DBusException {
        sessionConnection.addSigHandler(signalType, dbusObject, signalHandler);
    }
}
