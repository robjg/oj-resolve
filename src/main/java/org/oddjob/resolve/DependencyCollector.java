package org.oddjob.resolve;

import org.oddjob.resolve.types.DependencyContainer;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

public interface DependencyCollector {

    CollectResult collectDependencies(DependencyContainer dependencies) ;

    List<RemoteRepository> getRemoteRepositories();
}
