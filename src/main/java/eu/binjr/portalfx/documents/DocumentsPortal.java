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

package eu.binjr.portalfx.documents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class DocumentsPortal {
    private static final String DOCUMENT_PORTAL_HOST_PATH = "document-portal.host-path";
    private static final Logger logger = LoggerFactory.getLogger(DocumentsPortal.class);

    public  Path toHostFsPath(Path sandboxPath) {
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
