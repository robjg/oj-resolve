package org.oddjob.maven.types;

import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.maven.session.ResolverSession;
import org.oddjob.state.ParentState;

import java.io.File;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class ResolverSessionTypeTest {

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
    }

    @Test
    public void testSessionWithOurConfiguration() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();

        File configFile = new File(getClass().getResource(
                "/oddjob/Session/session-with-config.xml").getFile());

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
                .setMirroredRepositories(Arrays.asList(remoteRepository))
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
    public void testSessionWithSettings() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();

        File configFile = new File(getClass().getResource(
                "/oddjob/Session/session-with-settings.xml").getFile());

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
                .setMirroredRepositories(Arrays.asList(remoteRepository))
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
