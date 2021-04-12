package org.oddjob.resolve;

import org.eclipse.aether.resolution.ArtifactResult;

import java.util.Optional;

public interface ArtifactResultProcessor<T> {

    Optional<T> process(ArtifactResult artifactResult);
}
