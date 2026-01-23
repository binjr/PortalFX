package eu.binjr.portalfx.xdg;

import org.freedesktop.dbus.annotations.DBusBoundProperty;
import org.freedesktop.dbus.annotations.DBusProperty;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.UInt32;

public interface XdgPortalInterface extends DBusInterface, Properties {

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "Version")
    UInt32 getVersion();

}
