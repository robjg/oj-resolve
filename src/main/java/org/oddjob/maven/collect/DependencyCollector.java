package org.oddjob.maven.collect;

import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.repository.RemoteRepository;
import org.oddjob.maven.types.DependencyContainer;

import java.util.List;

public interface DependencyCollector {

    CollectResult collectDependencies(DependencyContainer dependencies) ;

    List<RemoteRepository> getRemoteRepositories();
}
