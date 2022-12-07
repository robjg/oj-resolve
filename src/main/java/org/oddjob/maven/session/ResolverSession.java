package org.oddjob.maven.session;

import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.RemoteRepositoryManager;

/**
 * What's required to resolve dependencies. Abstraction of Ants
 * {@link org.apache.maven.resolver.internal.ant.AntRepoSys}.
 */
public interface ResolverSession {

    Settings getSettings();

    RepositorySystemSession getSession();

    RepositorySystem getSystem();

    RemoteRepositoryManager getRemoteRepoMan();
}
