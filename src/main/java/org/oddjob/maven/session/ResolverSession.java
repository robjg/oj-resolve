package org.oddjob.maven.session;

import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;

/**
 * What's required to resolve dependencies. Abstraction of Ants
 * {@code org.apache.maven.resolver.internal.ant.AntRepoSys}.
 */
public interface ResolverSession {

    Settings getSettings();

    RepositorySystemSession getSession();

    RepositorySystem getSystem();
}
