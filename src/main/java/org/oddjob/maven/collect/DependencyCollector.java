package org.oddjob.maven.collect;

import org.eclipse.aether.collection.CollectResult;
import org.oddjob.maven.types.DependencyContainer;

/**
 * Abstraction for Collecting Dependencies.
 * <p>
 * In Ant this is in {@code AntRepoSys}
 *
 */
public interface DependencyCollector {

    CollectResult collectDependencies(DependencyContainer dependencies) ;
}
