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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResolveJob implements Runnable, ArooaSessionAware {

    private static final Logger logger = LoggerFactory.getLogger(ResolveJob.class);

    private volatile String name;

    private volatile ResolverSession resolverSession;

    private final List<RemoteRepository> remoteRepositories = new ArrayList<>();

    private volatile DependencyContainer dependencies;

    private volatile List<File> resolvedFiles;

    private volatile boolean noSettingsRepos;

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

        DependencyCollectorBuilder dependencyCollectorBuilder =
                DependencyCollectorBuilder.from(resolverSession);

        Optional.ofNullable(this.remoteRepositories)
                .ifPresent(repositories -> repositories.forEach(dependencyCollectorBuilder::withRepo));

        if (!noSettingsRepos) {
            dependencyCollectorBuilder.withSettingsRepos();
        }
        if (!noDefaultRepos) {
            dependencyCollectorBuilder.withDefaultRepos();
        }

        DependencyCollector dependencyCollector = dependencyCollectorBuilder
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
        return remoteRepositories.get(0);
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
