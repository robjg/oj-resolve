package org.oddjob.maven.session;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.*;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.util.repository.*;
import org.oddjob.maven.props.RepoSessionProperties;
import org.oddjob.maven.resolve.LoggingTransferListener;
import org.oddjob.maven.settings.SettingsBuilder;
import org.oddjob.maven.types.Authentication;
import org.oddjob.maven.types.Mirror;
import org.oddjob.maven.types.Proxy;
import org.oddjob.maven.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Builds an {@link ResolverSession} which is everything the {@link org.oddjob.maven.jobs.ResolveJob}
 * needs to resolve a dependency. Most of this code was copied from {@code AntRepoSys}.
 */
public class ResolverSessionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ResolverSessionBuilder.class);

    public static String LOCAL_REPO_PROPERTY = "maven.repo.local";

    private final RepoSessionProperties sessionProperties;

    private Settings settings;

    private File localRepo;

    private final List<Mirror> mirrors = new CopyOnWriteArrayList<>();

    private final List<Proxy> proxies = new CopyOnWriteArrayList<>();

    private final List<Authentication> authentications = new CopyOnWriteArrayList<>();

    private boolean offline;

    private ResolverSessionBuilder(RepoSessionProperties sessionProperties) {
        this.sessionProperties = Objects.requireNonNull(sessionProperties);
    }

    public static ResolverSessionBuilder from(RepoSessionProperties sessionProperties) {
        return new ResolverSessionBuilder(sessionProperties);
    }

    public ResolverSessionBuilder withSettings(Settings settings) {
        this.settings = settings;
        return this;
    }

    public ResolverSessionBuilder withLocalRepo(File localRepo) {
        this.localRepo = localRepo;
        return this;
    }

    public ResolverSessionBuilder withMirrors(Collection<Mirror> mirrors) {
        Optional.ofNullable(mirrors).ifPresent(this.mirrors::addAll);
        return this;
    }

    public ResolverSessionBuilder withProxies(Collection<Proxy> proxies) {
        Optional.ofNullable(proxies).ifPresent(this.proxies::addAll);
        return this;
    }

    public ResolverSessionBuilder withAuthentications(Collection<Authentication> authentications) {
        Optional.ofNullable(authentications).ifPresent(this.authentications::addAll);
        return this;
    }

    public ResolverSessionBuilder withOffline(boolean offline) {
        this.offline = offline;
        return this;
    }

    public ResolverSession build() throws SettingsBuildingException {

        logger.atDebug()
                .setMessage("Creating ResolveSession with sessionProperties {}, settings {}, " +
                            "localRepo {}, mirrors {}, proxies {}, authentications {}, offline {}")
                .addArgument(
                    sessionProperties)
                .addArgument(() -> SettingsBuilder.settingsToString(settings))
                        .addArgument(localRepo)
                .addArgument(mirrors)
                .addArgument(proxies)
                .addArgument(authentications)
                .addArgument(offline)
                .log();


        Settings settings = Optional.ofNullable(this.settings)
                .orElseGet(() -> {
                    try {
                        return SettingsBuilder.from(sessionProperties)
                                .build();
                    } catch (SettingsBuildingException e) {
                        throw new IllegalArgumentException(e);
                    }
                });


        RepositorySystem repoSys = new RepositorySystemSupplier().get();

        RepositorySystemSession session = getSession(settings, repoSys);

        return new Impl(settings, session, repoSys);
    }

    // Copied from AntRepoSys#getSession
    protected RepositorySystemSession getSession(Settings settings, RepositorySystem repoSys) {

        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        Map<Object, Object> configProps = new LinkedHashMap<>();
        configProps.put(ConfigurationProperties.USER_AGENT, getUserAgent(sessionProperties));
        configProps.putAll(sessionProperties.getAllProperties());
        processServerConfiguration(settings, configProps);

        session.setConfigProperties(configProps);
        session.setSystemProperties(sessionProperties.getSystemProperties());
        session.setUserProperties(sessionProperties.getUserProperties());
        session.setOffline(isOffline(settings));

        session.setProxySelector(getProxySelector(settings));
        session.setMirrorSelector(getMirrorSelector(settings));
        session.setAuthenticationSelector(getAuthSelector(settings));

        session.setCache(new DefaultRepositoryCache());

        session.setRepositoryListener(new LoggingRepositoryListener());
        session.setTransferListener(new LoggingTransferListener());

        File localRepo = Optional.ofNullable(this.localRepo)
                .orElseGet(() -> getDefaultLocalRepoDir(sessionProperties, settings));

        session.setLocalRepositoryManager(getLocalRepoMan(session, repoSys, localRepo));

        return session;
    }

    static void processServerConfiguration(Settings settings, Map<Object, Object> configProps) {
        for (Server server : settings.getServers()) {
            if (server.getConfiguration() != null) {
                Xpp3Dom dom = (Xpp3Dom) server.getConfiguration();
                for (int i = dom.getChildCount() - 1; i >= 0; i--) {
                    Xpp3Dom child = dom.getChild(i);
                    if ("wagonProvider".equals(child.getName())) {
                        dom.removeChild(i);
                    } else if ("httpHeaders".equals(child.getName())) {
                        configProps.put(ConfigurationProperties.HTTP_HEADERS + "." + server.getId(),
                                getHttpHeaders(child));
                    }
                }

                configProps.put("aether.connector.wagon.config." + server.getId(), dom);
            }

            configProps.put("aether.connector.perms.fileMode." + server.getId(), server.getFilePermissions());
            configProps.put("aether.connector.perms.dirMode." + server.getId(), server.getDirectoryPermissions());
        }
    }

    static Map<String, String> getHttpHeaders(Xpp3Dom dom) {
        Map<String, String> headers = new HashMap<>();
        for (int i = 0; i < dom.getChildCount(); i++) {
            Xpp3Dom child = dom.getChild(i);
            Xpp3Dom name = child.getChild("name");
            Xpp3Dom value = child.getChild("value");
            if (name != null && name.getValue() != null) {
                headers.put(name.getValue(), (value != null) ? value.getValue() : null);
            }
        }
        return Collections.unmodifiableMap(headers);
    }

    private String getUserAgent(RepoSessionProperties project) {

        return "Apache-Ant/" + project.getProperty("ant.version") +
                " (" +
                "Java " + System.getProperty("java.version") +
                "; " +
                System.getProperty("os.name") + " " + System.getProperty("os.version") +
                ")" +
                " Aether";
    }

    boolean isOffline(Settings settings) {

        return offline || settings.isOffline();
    }

    private ProxySelector getProxySelector(Settings settings) {
        DefaultProxySelector selector = new DefaultProxySelector();

        for (Proxy proxy : proxies) {
            selector.add(ConverterUtils.toProxy(proxy), proxy.getNonProxyHosts());
        }

        for (org.apache.maven.settings.Proxy proxy : settings.getProxies()) {
            AuthenticationBuilder auth = new AuthenticationBuilder();
            auth.addUsername(proxy.getUsername()).addPassword(proxy.getPassword());
            selector.add(new org.eclipse.aether.repository.Proxy(proxy.getProtocol(), proxy.getHost(),
                            proxy.getPort(), auth.build()),
                    proxy.getNonProxyHosts());
        }

        return selector;
    }

    // Copied from AntRepoSys#getMirrorSelector.
    private MirrorSelector getMirrorSelector(Settings settings) {
        DefaultMirrorSelector selector = new DefaultMirrorSelector();

        for (Mirror mirror : mirrors) {
            selector.add(mirror.getId(),
                    mirror.getUrl(),
                    mirror.getType(),
                    false,
                    false,
                    mirror.getMirrorOf(), null);
        }

        for (org.apache.maven.settings.Mirror mirror : settings.getMirrors()) {
            selector.add(String.valueOf(mirror.getId()),
                    mirror.getUrl(),
                    mirror.getLayout(),
                    false,
                    false,
                    mirror.getMirrorOf(),
                    mirror.getMirrorOfLayouts());
        }

        return selector;
    }

    private AuthenticationSelector getAuthSelector(Settings settings) {
        DefaultAuthenticationSelector selector = new DefaultAuthenticationSelector();

        Collection<String> ids = new HashSet<>();
        for (Authentication auth : authentications) {
            List<String> servers = auth.getServers();
            if (!servers.isEmpty()) {
                org.eclipse.aether.repository.Authentication a = ConverterUtils.toAuthentication(auth);
                for (String server : servers) {
                    if (ids.add(server)) {
                        selector.add(server, a);
                    }
                }
            }
        }

        for (Server server : settings.getServers()) {
            AuthenticationBuilder auth = new AuthenticationBuilder();
            auth.addUsername(server.getUsername()).addPassword(server.getPassword());
            auth.addPrivateKey(server.getPrivateKey(), server.getPassphrase());
            selector.add(server.getId(), auth.build());
        }

        return new ConservativeAuthenticationSelector(selector);
    }

    static LocalRepositoryManager getLocalRepoMan(RepositorySystemSession session,
                                                  RepositorySystem repoSys,
                                                  File localRepo) {

        org.eclipse.aether.repository.LocalRepository repo =
                new org.eclipse.aether.repository.LocalRepository(localRepo);

        return repoSys.newLocalRepositoryManager(session, repo);
    }

    static File getDefaultLocalRepoDir(RepoSessionProperties project, Settings settings) {
        String dir = project.getProperty(LOCAL_REPO_PROPERTY);

        if (dir != null) {
            logger.debug("Setting local repo from user property 'maven.repo.local'={}", dir);
            return new File(dir);
        }

        dir = settings.getLocalRepository();
        if (dir != null) {
            logger.debug("Setting local repo from settings, {}", dir);
            return new File(settings.getLocalRepository());
        }

        File localRepo = new File(new File(project.getProperty("user.home"), ".m2"), "repository");
        logger.debug("Setting local repo to default of {}", localRepo);
        return localRepo;
    }

    static class Impl implements ResolverSession {

        private final Settings settings;

        private final RepositorySystemSession session;

        private final RepositorySystem repoSys;

        Impl(Settings settings,
             RepositorySystemSession session,
             RepositorySystem repoSys) {
            this.settings = settings;
            this.session = session;
            this.repoSys = repoSys;
        }

        @Override
        public Settings getSettings() {
            return settings;
        }

        @Override
        public RepositorySystemSession getSession() {
            return session;
        }

        @Override
        public RepositorySystem getSystem() {
            return repoSys;
        }

        @Override
        public String toString() {
            return "ResolverSession{" +
                    "settings=" + SettingsBuilder.settingsToString(settings) +
                    ", session=" + ResolverSessionBuilder.toString(session) +
                    ", repoSys=" + repoSys +
                    '}';
        }
    }

    public static String toString(RepositorySystemSession repositorySystemSession) {

        return "RepositorySystemSession={localRepository=" + repositorySystemSession.getLocalRepository() +
            ", localRepositoryManager=" + repositorySystemSession.getLocalRepositoryManager() +
            ", isOffline=" + repositorySystemSession.isOffline() +
                ", ignoreArtifactDescriptorRepositories=" + repositorySystemSession.isIgnoreArtifactDescriptorRepositories() +
                ", resolutionErrorPolicy=" + repositorySystemSession.getResolutionErrorPolicy() +
                ", artifactDescriptorPolicy=" + repositorySystemSession.getArtifactDescriptorPolicy() +
                ", checksumPolicy=" + repositorySystemSession.getChecksumPolicy() +
                ", updatePolicy=" + repositorySystemSession.getUpdatePolicy() +
                ", workspaceReader=" + repositorySystemSession.getWorkspaceReader() +
                ", repositoryListener=" + repositorySystemSession.getRepositoryListener() +
                ", transferListener=" + repositorySystemSession.getTransferListener() +
                ", systemProperties count=" + Optional.ofNullable(
                        repositorySystemSession.getSystemProperties()).map(Map::size).orElse(0) +
                ", userProperties count=" + Optional.ofNullable(
                        repositorySystemSession.getUserProperties()).map(Map::size).orElse(0) +
                ", configProperties count=" + Optional.ofNullable(
                        repositorySystemSession.getConfigProperties()).map(Map::size).orElse(0) +
                ", mirrorSelector=" + repositorySystemSession.getMirrorSelector() +
                ", proxySelector=" + repositorySystemSession.getProxySelector() +
                ", authenticationSelector=" + repositorySystemSession.getAuthenticationSelector() +
                ", artifactTypeRegistry=" + repositorySystemSession.getArtifactTypeRegistry() +
                ", dependencyTraverser=" + repositorySystemSession.getDependencyTraverser() +
                ", dependencyManager=" + repositorySystemSession.getDependencyManager() +
                ", dependencySelector=" + repositorySystemSession.getDependencySelector() +
                ", versionFilter=" + repositorySystemSession.getVersionFilter() +
                ", dependencyGraphTransformer=" + repositorySystemSession.getDependencyGraphTransformer() +
                ", data=" + repositorySystemSession.getData() +
                ", cache=" + repositorySystemSession.getCache() + "}";

    }
}
