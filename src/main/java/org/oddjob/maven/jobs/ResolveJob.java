package org.oddjob.maven.jobs;

import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.resolution.ArtifactResult;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.maven.collect.DependencyCollector;
import org.oddjob.maven.collect.DependencyCollectorBuilder;
import org.oddjob.maven.resolve.DependencyResolver;
import org.oddjob.maven.resolve.DependencyResolverBuilder;
import org.oddjob.maven.session.ResolverSession;
import org.oddjob.maven.types.DependencyContainer;
import org.oddjob.maven.types.RemoteRepository;
import org.oddjob.maven.types.ResolverSessionType;
import org.oddjob.maven.util.DependencyGraphLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @oddjob.description Resolves dependencies to a list of files downloaded to the local repo.
 *
 * @oddjob.example
 *
 * Simple resolve.
 * {@oddjob.xml.resource oddjob/Resolve/resolve-with-defaults.xml}
 */
public class ResolveJob implements Runnable, ArooaSessionAware {

    private static final Logger logger = LoggerFactory.getLogger(ResolveJob.class);

    /**
     * @oddjob.property
     * @oddjob.description The name of this job.
     * @oddjob.required No.
     */
    private volatile String name;

    /**
     * @oddjob.property
     * @oddjob.description The Session to use. See {@link org.oddjob.maven.types.ResolverSessionType}.
     * @oddjob.required No, All defaults will be used.
     */
    private volatile ResolverSession resolverSession;

    /**
     * @oddjob.property
     * @oddjob.description Optional repositories to use.
     * @oddjob.required No, Defaults will be used.
     */
    private final List<RemoteRepository> remoteRepositories = new ArrayList<>();

    /**
     * @oddjob.property
     * @oddjob.description The dependencies to resolve. See {@link org.oddjob.maven.types.DependencyContainer}.
     * @oddjob.required Yes.
     */
    private volatile DependencyContainer dependencies;

    /**
     * @oddjob.property
     * @oddjob.description A List of resolved files.
     * @oddjob.required R/O.
     */
    private volatile List<File> resolvedFiles;

    /**
     * @oddjob.property
     * @oddjob.description A List of resolved paths.
     * @oddjob.required R/O.
     */
    private volatile List<Path> resolvedPaths;

    /**
     * @oddjob.property
     * @oddjob.description Use repos from the settings (true/false).
     * @oddjob.required No. Settings repos will be used.
     */
    private volatile boolean noSettingsRepos;

    /**
     * @oddjob.property
     * @oddjob.description Use default repos (true/false).
     * @oddjob.required No. The default repos will be used.
     */
    private volatile boolean noDefaultRepos;

    private volatile ArooaSession arooaSession;

    @Override
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }

    @Override
    public void run() {

        ResolverSession resolverSession = Optional.ofNullable(this.resolverSession)
                .orElseGet(() -> {
                    ResolverSessionType resolverSessionType = new ResolverSessionType();
                    resolverSessionType.setArooaSession(Objects.requireNonNull(arooaSession,
                            "No ArooaSession"));

                    try {
                        ResolverSession rs = resolverSessionType.toValue();
                        this.resolverSession = rs;
                        return rs;
                    } catch (ArooaConversionException e) {
                        throw new ArooaConfigurationException(e);
                    }
                });

        DependencyCollector dependencyCollector =
                DependencyCollectorBuilder.from(resolverSession)
                        .withNoDefaultRepos(noDefaultRepos)
                        .withNoSettingsRepos(noSettingsRepos)
                        .withRepos(this.remoteRepositories)
                        .build();

        CollectResult result = dependencyCollector.collectDependencies(dependencies);

        if (logger.isDebugEnabled()) {
            result.getRoot().accept(new DependencyGraphLogger());
        }

        DependencyResolver dependencyResolver = DependencyResolverBuilder.from(resolverSession)
                .build();

        List<ArtifactResult> results = dependencyResolver.resolve(result.getRoot());

        this.resolvedFiles = results.stream()
                .map(ar -> ar.getArtifact().getFile())
                .collect(Collectors.toList());

        this.resolvedPaths = resolvedFiles.stream()
                .map(File::toPath)
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResolverSession getResolverSession() {
        return resolverSession;
    }

    public void setResolverSession(ResolverSession resolverSession) {
        this.resolverSession = resolverSession;
    }

    public RemoteRepository getRemoteRepositories(int index) {
        return remoteRepositories.get(index);
    }

    public void setRemoteRepositories(int index, RemoteRepository remoteRepository) {
        new ListSetterHelper<>(this.remoteRepositories).set(index,  remoteRepository);
    }

    public DependencyContainer getDependencies() {
        return dependencies;
    }

    public void setDependencies(DependencyContainer dependencies) {
        this.dependencies = dependencies;
    }

    public List<File> getResolvedFiles() {
        return resolvedFiles;
    }

    public List<Path> getResolvedPaths() {
        return resolvedPaths;
    }

    /**
     * @oddjob.property
     * @oddjob.description An array of resolved files. A convenience to make this
     * easier to use with an {@link org.oddjob.util.URLClassLoaderType}.
     * @oddjob.required R/O.
     */
    @SuppressWarnings("JavadocReference")
    public File[] getResolvedFilesArray() {
        return Optional.ofNullable(resolvedFiles)
                .map(rf -> rf.toArray(new File[0]))
                .orElse(null);
    }

    /**
     * @oddjob.property
     * @oddjob.description An array of resolved paths. A convenience to make this
     * easier to use.
     * @oddjob.required R/O.
     */
    public Path[] getResolvedPathsArray() {
        return Optional.ofNullable(resolvedPaths)
                .map(rf -> rf.toArray(new Path[0]))
                .orElse(null);
    }

    public boolean isNoSettingsRepos() {
        return noSettingsRepos;
    }

    public void setNoSettingsRepos(boolean noSettingsRepos) {
        this.noSettingsRepos = noSettingsRepos;
    }

    public boolean isNoDefaultRepos() {
        return noDefaultRepos;
    }

    public void setNoDefaultRepos(boolean noDefaultRepos) {
        this.noDefaultRepos = noDefaultRepos;
    }

    @Override
    public String toString() {
        return Optional.ofNullable(this.name).orElseGet(() -> getClass().getSimpleName());
    }
}
