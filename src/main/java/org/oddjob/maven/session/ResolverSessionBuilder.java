package org.oddjob.maven.session;

import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.*;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.log.Logger;
import org.eclipse.aether.transport.classpath.ClasspathTransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.*;
import org.oddjob.maven.props.RepoSessionProperties;
import org.oddjob.maven.resolve.AntServiceLocatorErrorHandler;
import org.oddjob.maven.resolve.LoggingTransferListener;
import org.oddjob.maven.settings.SettingsBuilder;
import org.oddjob.maven.types.Authentication;
import org.oddjob.maven.types.Mirror;
import org.oddjob.maven.types.Proxy;
import org.oddjob.maven.util.ConverterUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResolverSessionBuilder {

    private static final ModelBuilder MODEL_BUILDER = new DefaultModelBuilderFactory().newInstance();

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

        Settings settings = Optional.ofNullable(this.settings)
                .orElseGet(() -> {
                    try {
                        return SettingsBuilder.from(sessionProperties)
                                .build();
                    } catch (SettingsBuildingException e) {
                        throw new IllegalArgumentException(e);
                    }
                });

        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.setErrorHandler(new AntServiceLocatorErrorHandler());
        locator.setServices(Logger.class, new LoggerDelegate());
        locator.setServices(ModelBuilder.class, MODEL_BUILDER);
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        locator.addService(TransporterFactory.class, ClasspathTransporterFactory.class);

        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        Map<Object, Object> configProps = new LinkedHashMap<>();
        configProps.put(ConfigurationProperties.USER_AGENT, getUserAgent(sessionProperties));
        configProps.putAll(sessionProperties.getAllProperties());
        processServerConfiguration(settings, configProps);
        session.setConfigProperties(configProps);

        session.setOffline(isOffline(settings));
        session.setUserProperties(sessionProperties.getUserProperties());

        session.setProxySelector(getProxySelector(settings));
        session.setMirrorSelector(getMirrorSelector(settings));
        session.setAuthenticationSelector(getAuthSelector(settings));

        session.setCache(new DefaultRepositoryCache());

        session.setRepositoryListener(new LoggingRepositoryListener());
        session.setTransferListener(new LoggingTransferListener());

        File localRepo = Optional.ofNullable(this.localRepo)
                .orElseGet(() -> getDefaultLocalRepoDir(sessionProperties, settings));

        RepositorySystem repoSys = Optional.ofNullable(locator.getService(RepositorySystem.class))
                .orElseThrow(() -> new IllegalStateException("Failed to find an " +
                        RepositorySystem.class.getName() + " in " + locator));

        session.setLocalRepositoryManager(getLocalRepoMan(session, repoSys, localRepo));

        RemoteRepositoryManager remoteRepoMan = Objects.requireNonNull(
                locator.getService( RemoteRepositoryManager.class ),
                "The repository system could not be initialized" );

        return new Impl(settings, session, repoSys, remoteRepoMan);
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
        StringBuilder buffer = new StringBuilder(128);

        buffer.append("Apache-Ant/").append(project.getProperty("ant.version"));
        buffer.append(" (");
        buffer.append("Java ").append(System.getProperty("java.version"));
        buffer.append("; ");
        buffer.append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version"));
        buffer.append(")");
        buffer.append(" Aether");

        return buffer.toString();
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

    private MirrorSelector getMirrorSelector(Settings settings) {
        DefaultMirrorSelector selector = new DefaultMirrorSelector();

        for (Mirror mirror : mirrors) {
            selector.add(mirror.getId(), mirror.getUrl(), mirror.getType(), false, mirror.getMirrorOf(), null);
        }

        for (org.apache.maven.settings.Mirror mirror : settings.getMirrors()) {
            selector.add(String.valueOf(mirror.getId()), mirror.getUrl(), mirror.getLayout(), false,
                    mirror.getMirrorOf(), mirror.getMirrorOfLayouts());
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
        String dir = project.getProperty("maven.repo.local");

        if (dir != null) {
            return new File(dir);
        }

        if (settings.getLocalRepository() != null) {
            return new File(settings.getLocalRepository());
        }

        return new File(new File(project.getProperty("user.home"), ".m2"), "repository");
    }

    static class Impl implements ResolverSession {

        private final Settings settings;

        private final RepositorySystemSession session;

        private final RepositorySystem repoSys;

        private final RemoteRepositoryManager remoteRepoMan;

        Impl(Settings settings,
             RepositorySystemSession session,
             RepositorySystem repoSys,
             RemoteRepositoryManager remoteRepoMan) {
            this.settings = settings;
            this.session = session;
            this.repoSys = repoSys;
            this.remoteRepoMan = remoteRepoMan;
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
        public RemoteRepositoryManager getRemoteRepoMan() {
            return remoteRepoMan;
        }

        @Override
        public String toString() {
            return "ResolverSession{" +
                    "settings=" + SettingsBuilder.settingsToString(settings) +
                    ", session=" + session +
                    ", repoSys=" + repoSys +
                    '}';
        }
    }

}
