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

import eu.binjr.portalfx.xdg.XdgPortalInterface;
import org.freedesktop.dbus.annotations.DBusBoundProperty;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusProperty;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

/**
 * Data model for the org.freedesktop.portal.Settings interface
 * See specifications at:
 * https://flatpak.github.io/xdg-desktop-portal/docs/doc-org.freedesktop.portal.Settings.html#methods
 */
@DBusInterfaceName("org.freedesktop.portal.Settings")
public interface XdgSettingsInterface extends XdgPortalInterface {
    <T> Variant<T> ReadOne(String namespace, String key);

    <T> Map<String, Map<String, Variant<T>>> ReadAll(String[] namespaces);

    class SettingChanged<T> extends DBusSignal {
        private final String namespace;
        private final String key;
        private final Variant<T> value;

        public SettingChanged(String _path, String namespace, String key, Variant<T> value) throws DBusException {
            super(_path, namespace, key, value);
            this.namespace = namespace;
            this.key = key;
            this.value = value;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getKey() {
            return key;
        }

        public Variant<T> getValue() {
            return value;
        }
    }

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "version")
    UInt32 getVersion();
}
