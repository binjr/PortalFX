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

package eu.binjr.portalfx;

import eu.binjr.portalfx.documents.DocumentsPortal;
import eu.binjr.portalfx.settings.SettingsPortal;
import eu.binjr.portalfx.settings.JavaFxSettingsPortal;
import eu.binjr.portalfx.settings.XdgSettingsInterface;
import eu.binjr.portalfx.settings.XdgSettingsPortal;
import eu.binjr.portalfx.xdg.XdgPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Portal {
    private static final Logger logger = LoggerFactory.getLogger(Portal.class);

    private static class SettingsHolder {
        private final static SettingsPortal SETTINGS_PORTAL;

        static {
            var p = XdgPortal.of(XdgSettingsPortal.class);
            SETTINGS_PORTAL = p.isPresent() ? p.get() : new JavaFxSettingsPortal();
        }
    }

    private static class DocumentsHolder {
        private final static DocumentsPortal DOCUMENT_PORTAL = new DocumentsPortal();
    }

    public static SettingsPortal settings() {
        return SettingsHolder.SETTINGS_PORTAL;
    }

    public static DocumentsPortal documents() {
        return DocumentsHolder.DOCUMENT_PORTAL;
    }

}

