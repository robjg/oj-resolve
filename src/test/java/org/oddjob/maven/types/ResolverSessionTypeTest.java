package org.oddjob.maven.types;

import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.maven.session.ResolverSession;
import org.oddjob.maven.session.ResolverSessionBuilder;
import org.oddjob.state.ParentState;

import java.io.File;
import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


class ResolverSessionTypeTest {

    public static class SessionCapture implements Runnable {

        private ResolverSession resolverSession;

        public ResolverSession getResolverSession() {
            return resolverSession;
        }

        public void setResolverSession(ResolverSession resolverSession) {
            this.resolverSession = resolverSession;
        }

        @Override
        public void run() {

        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    static Object localRepo;


    @BeforeAll
    static void setUpAll() {
        // Intellij sets this property which causes tests where
        // we change the repo to fail.
        localRepo = System.getProperties()
                .remove(ResolverSessionBuilder.LOCAL_REPO_PROPERTY);
    }

    @AfterAll
    static void tearDownAll() {
        if (localRepo != null) {
            System.getProperties()
                    .put(ResolverSessionBuilder.LOCAL_REPO_PROPERTY, localRepo);
        }
    }

    @Test
    void testSessionWithOurConfiguration() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();

        File configFile = new File(Objects.requireNonNull(getClass().getResource(
                "/oddjob/Session/session-with-config.xml")).getFile());

        oddjob.setFile(configFile);
        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        ResolverSession resolverSession = new OddjobLookup(oddjob)
                .lookup("sessionCapture.resolverSession", ResolverSession.class);

        assertThat(resolverSession.getSession().getLocalRepository().getBasedir(),
                is(new File(configFile.getParentFile(), "myrepo")));

        RemoteRepository remoteRepository = new RemoteRepository.Builder("foo", "default",
                "http:///myrepo.com").build();

        RemoteRepository mirrorRepository = new RemoteRepository.Builder("planetmirror.com", "default",
                "http://downloads.planetmirror.com/pub/maven2")
                .setMirroredRepositories(Collections.singletonList(remoteRepository))
                .build();

        assertThat(resolverSession.getSession().getMirrorSelector().getMirror(remoteRepository),
                is(mirrorRepository));

        AuthenticationBuilder auth = new AuthenticationBuilder();
        auth.addUsername("proxyuser").addPassword("somepassword");

        Proxy proxy = new Proxy("http", "proxy.somewhere.com", 8080, auth.build());

        assertThat(resolverSession.getSession().getProxySelector().getProxy(remoteRepository),
                is(proxy));
    }

    @Test
    void testSessionWithSettings() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();

        File configFile = new File(Objects.requireNonNull(getClass().getResource(
                "/oddjob/Session/session-with-settings.xml")).getFile());

        oddjob.setFile(configFile);
        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        ResolverSession resolverSession = new OddjobLookup(oddjob)
                .lookup("sessionCapture.resolverSession", ResolverSession.class);

        assertThat(resolverSession.getSession().getLocalRepository().getBasedir(),
                is(new File(configFile.getParentFile(), ".m2/repository")));

        RemoteRepository remoteRepository = new RemoteRepository.Builder("foo", "default",
                "http:///myrepo.com").build();

        RemoteRepository mirrorRepository = new RemoteRepository.Builder("planetmirror.com", "default",
                "http://downloads.planetmirror.com/pub/maven2")
                .setMirroredRepositories(Collections.singletonList(remoteRepository))
                .build();

        assertThat(resolverSession.getSession().getMirrorSelector().getMirror(remoteRepository),
                is(mirrorRepository));

        AuthenticationBuilder auth = new AuthenticationBuilder();
        auth.addUsername("proxyuser").addPassword("somepassword");

        Proxy proxy = new Proxy("http", "proxy.somewhere.com", 8080, auth.build());

        assertThat(resolverSession.getSession().getProxySelector().getProxy(remoteRepository),
                is(proxy));
    }

}
