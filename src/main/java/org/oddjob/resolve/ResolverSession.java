package org.oddjob.resolve;

import org.apache.maven.model.Repository;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

public interface ResolverSession {

    Settings getSettings();

    RepositorySystemSession getSession();

    RepositorySystem getSystem();

}
