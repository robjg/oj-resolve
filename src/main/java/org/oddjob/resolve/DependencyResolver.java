package org.oddjob.resolve;

import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.ArtifactResult;

import java.util.List;

public interface DependencyResolver {

    List<ArtifactResult> resolve(DependencyNode dependencyNode);
}
