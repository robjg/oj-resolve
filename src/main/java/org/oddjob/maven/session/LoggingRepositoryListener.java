package org.oddjob.maven.session;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * Logs repository events like installed and unresolved artifacts and metadata.
 */
class LoggingRepositoryListener
        extends AbstractRepositoryListener {

    private static final Logger logger = LoggerFactory.getLogger(LoggingRepositoryListener.class);

    @Override
    public void artifactInstalling(RepositoryEvent event) {
        logger.info("Installing " + event.getArtifact().getFile() + " to " + event.getFile());
    }

    @Override
    public void metadataInstalling(RepositoryEvent event) {
        logger.info("Installing " + event.getMetadata() + " to " + event.getFile());
    }

    @Override
    public void metadataResolved(RepositoryEvent event) {
        Exception e = event.getException();
        if (e != null) {
            if (e instanceof MetadataNotFoundException) {
                logger.debug(e.getMessage());
            } else {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    public void metadataInvalid(RepositoryEvent event) {
        Exception exception = event.getException();

        StringBuilder buffer = new StringBuilder(256);
        buffer.append("The metadata ");
        if (event.getMetadata().getFile() != null) {
            buffer.append(event.getMetadata().getFile());
        } else {
            buffer.append(event.getMetadata());
        }

        if (exception instanceof FileNotFoundException) {
            buffer.append(" is inaccessible");
        } else {
            buffer.append(" is invalid");
        }

        if (exception != null) {
            buffer.append(": ");
            buffer.append(exception.getMessage());
        }

        logger.warn(buffer.toString(), exception);
    }

    @Override
    public void artifactDescriptorInvalid(RepositoryEvent event) {
        logger.warn("The POM for " + event.getArtifact() + " is invalid"
                        + ", transitive dependencies (if any) will not be available: "
                        + event.getException().getMessage(),
                event.getException());
    }

    @Override
    public void artifactDescriptorMissing(RepositoryEvent event) {
        logger.warn("The POM for " + event.getArtifact() + " is missing, no dependency information available");
    }
}
