package org.oddjob.resolve;

import org.oddjob.resolve.types.Dependencies;
import org.oddjob.resolve.types.Dependency;
import org.oddjob.resolve.types.DependencyContainer;
import org.oddjob.resolve.types.Exclusion;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DependencyCollectorBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DependencyCollector.class);

    private final ResolverSession resolverSession;

    private final List<RemoteRepository> repos = new ArrayList<>();

    private DependencyCollectorBuilder(ResolverSession resolverSession) {
        this.resolverSession = Objects.requireNonNull(resolverSession);
    }

    public static DependencyCollectorBuilder from(ResolverSession resolverSession) {
        return new DependencyCollectorBuilder(resolverSession);
    }

    public DependencyCollectorBuilder withDefaultRepos() {

        org.oddjob.resolve.types.RemoteRepository repo = new org.oddjob.resolve.types.RemoteRepository();
        repo.setId( "central" );
        repo.setUrl( "https://repo1.maven.org/maven2/" );

        repos.add(ConverterUtils.toRepository(repo));
        return this;
    }

    public DependencyCollectorBuilder withSettingsRepos() {

        Settings settings = resolverSession.getSettings();
        List<String> activeProfiles = settings.getActiveProfiles();
        for ( String profileId : activeProfiles )
        {
            Profile profile = settings.getProfilesAsMap().get( profileId );
            for ( Repository repository : profile.getRepositories() )
            {
                String id = repository.getId();
                org.oddjob.resolve.types.RemoteRepository repo = new org.oddjob.resolve.types.RemoteRepository();
                repo.setId( id );
                repo.setUrl( repository.getUrl() );
                if ( repository.getReleases() != null )
                {
                    RepositoryPolicy repositoryPolicy = repository.getReleases();
                    org.oddjob.resolve.types.RemoteRepository.Policy policy = new org.oddjob.resolve.types.RemoteRepository.Policy();
                    policy.setEnabled( repositoryPolicy.isEnabled() );
                    if ( repositoryPolicy.getChecksumPolicy() != null )
                    {
                        policy.setChecksums( repositoryPolicy.getChecksumPolicy() );
                    }
                    if ( repositoryPolicy.getUpdatePolicy() != null )
                    {
                        policy.setUpdates( repositoryPolicy.getUpdatePolicy() );
                    }
                    repo.setReleases( policy );
                }
                if ( repository.getSnapshots() != null )
                {
                    RepositoryPolicy repositoryPolicy = repository.getSnapshots();
                    org.oddjob.resolve.types.RemoteRepository.Policy policy = new org.oddjob.resolve.types.RemoteRepository.Policy();
                    policy.setEnabled( repositoryPolicy.isEnabled() );
                    if ( repositoryPolicy.getChecksumPolicy() != null )
                    {
                        policy.setChecksums( repositoryPolicy.getChecksumPolicy() );
                    }
                    if ( repositoryPolicy.getUpdatePolicy() != null )
                    {
                        policy.setUpdates( repositoryPolicy.getUpdatePolicy() );
                    }
                    repo.setSnapshots( policy );
                }

                repos.add( ConverterUtils.toRepository(repo) );
            }
        }
        return this;
    }

    public DependencyCollectorBuilder withRepo(org.oddjob.resolve.types.RemoteRepository repo) {

        repos.add( ConverterUtils.toRepository(repo) );
        return this;
    }


    public DependencyCollector build() {
        return new Impl(this);
    }

    static class Impl implements DependencyCollector {

        private final ResolverSession resolverSession;

        private final List<RemoteRepository> repos;

        Impl(DependencyCollectorBuilder builder) {
            this.resolverSession = builder.resolverSession;
            this.repos = new ArrayList<>(builder.repos);
        }

        @Override
        public CollectResult collectDependencies(DependencyContainer dependencies) {
            Objects.requireNonNull(dependencies, "There must be a Dependency to collect");

            RepositorySystemSession session = resolverSession.getSession();

            Objects.requireNonNull(session.getLocalRepositoryManager(),
                    "Session must have Local Repository Manager set");

            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRequestContext("project");

            for (RemoteRepository repo : repos) {
                logger.debug("Using remote repository " + repo);
                collectRequest.addRepository(repo);
            }

            // This is messy
            if (dependencies instanceof Dependency) {
                Dependencies deps = new Dependencies();
                deps.addDependency((Dependency) dependencies);
                dependencies = deps;
            }

            populateCollectRequest(collectRequest, session, (Dependencies) dependencies, Collections.emptyList());

            logger.debug("Collecting dependencies");

            try {
                return  resolverSession.getSystem().collectDependencies(session, collectRequest);
            } catch (DependencyCollectionException e) {
                throw new ResolveException(e);
            }
        }

        @Override
        public List<RemoteRepository> getRemoteRepositories() {
            return Collections.unmodifiableList(repos);
        }

        private void populateCollectRequest(CollectRequest collectRequest, RepositorySystemSession session,
                                            Dependencies dependencies, List<Exclusion> exclusions) {
            List<Exclusion> globalExclusions = exclusions;
            if (!dependencies.getExclusions().isEmpty()) {
                globalExclusions = new ArrayList<>(exclusions);
                globalExclusions.addAll(dependencies.getExclusions());
            }

            Collection<String> ids = new HashSet<>();

            for (DependencyContainer container : dependencies.getDependencyContainers()) {
                container.validate();
                if (container instanceof Dependency) {
                    Dependency dep = (Dependency) container;
                    ids.add(dep.getVersionlessKey());
                    collectRequest.addDependency(ConverterUtils.toDependency(dep, globalExclusions, session));
                } else {
                    populateCollectRequest(collectRequest, session, (Dependencies) container, globalExclusions);
                }
            }

            if (dependencies.getFile() != null) {
                List<Dependency> deps = readDependencies(dependencies.getFile());
                for (Dependency dependency : deps) {
                    if (ids.contains(dependency.getVersionlessKey())) {
                        logger.debug("Ignoring dependency " + dependency.getVersionlessKey() + " from "
                                + dependencies.getFile() + ", already declared locally");
                        continue;
                    }
                    collectRequest.addDependency(ConverterUtils.toDependency(dependency, globalExclusions, session));
                }
            }
        }

        private List<Dependency> readDependencies(File file) {
            List<Dependency> dependencies = new ArrayList<>();
            try {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        int comment = line.indexOf('#');
                        if (comment >= 0) {
                            line = line.substring(0, comment);
                        }
                        line = line.trim();
                        if (line.length() <= 0) {
                            continue;
                        }
                        Dependency dependency = new Dependency();
                        dependency.setCoords(line);
                        dependencies.add(dependency);
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot read " + file, e);
            }
            return dependencies;
        }
    }
}
