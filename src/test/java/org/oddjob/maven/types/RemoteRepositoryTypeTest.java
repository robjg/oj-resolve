package org.oddjob.maven.types;

import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.junit.Test;
import org.oddjob.maven.util.ConverterUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RemoteRepositoryTypeTest {

    @Test
    public void testDefaults() {

        RemoteRepository repo = new RemoteRepository();

        org.eclipse.aether.repository.RemoteRepository result = ConverterUtils.toRepository(repo);

        assertThat(result.getContentType(), is("default"));

        Authentication authentication = result.getAuthentication();


        RepositoryPolicy releasePolicy = result.getPolicy(false);

        assertThat(releasePolicy.isEnabled(), is(true));
        assertThat(releasePolicy.getUpdatePolicy(), is("daily"));
        assertThat(releasePolicy.getChecksumPolicy(), is("warn"));

        RepositoryPolicy snapshotPolicy = result.getPolicy(true);

        assertThat(snapshotPolicy.isEnabled(), is(false));
        assertThat(snapshotPolicy.getUpdatePolicy(), is("daily"));
        assertThat(snapshotPolicy.getChecksumPolicy(), is("warn"));
    }
}
