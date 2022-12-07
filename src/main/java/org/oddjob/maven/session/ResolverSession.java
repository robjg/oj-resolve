package org.oddjob.maven.session;

import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;

/**
 *
 */
public interface ResolverSession {

    Settings getSettings();

    RepositorySystemSession getSession();

    RepositorySystem getSystem();

}
