package org.oddjob.maven.resolve;

import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.maven.OurDirs;
import org.oddjob.maven.session.ResolverSession;
import org.oddjob.state.ParentState;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class ResolveJobRepoTest {


    @Test
    public void testResolveUsingRepo() throws Exception {

        Path localRepo = OurDirs.workPathDir(getClass().getSimpleName() + "/repo", true);

        Properties properties = new Properties();
        properties.setProperty("local.repo", localRepo.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setProperties(properties);
        oddjob.setFile(new File(getClass().getResource(
                "/oddjob/Resolve/resolve-from-repo.xml").getFile()));
        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        ResolverSession resolverSession = lookup.lookup("resolve.resolverSession",
                ResolverSession.class);

        List<File> files = lookup.lookup("resolve.resolvedFiles",
                List.class);

        File repoDir = resolverSession.getSession().getLocalRepository().getBasedir();

        File file1 = new File(repoDir,
                "test/oj/resolve/a/1.2.3/a-1.2.3.jar");
        File file2 = new File(repoDir,
                "test/oj/resolve/b/4.5.6/b-4.5.6.jar");
        File file3 = new File(repoDir,
                "test/oj/resolve/c/7.8.9/c-7.8.9.jar");

        assertThat(files, contains(file1, file2, file3));

    }

    @Test
    public void testResolveBasicAuthentication() throws Exception {

        Path localRepo = OurDirs.workPathDir(getClass().getSimpleName() + "/repo", true);

        Properties properties = new Properties();
        properties.setProperty("local.repo", localRepo.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setProperties(properties);
        oddjob.setFile(new File(getClass().getResource(
                "/oddjob/Resolve/resolve-basic-authentication.xml").getFile()));
        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        ResolverSession resolverSession = lookup.lookup("resolve.resolverSession",
                ResolverSession.class);

        List<File> files = lookup.lookup("resolve.resolvedFiles",
                List.class);

        File repoDir = resolverSession.getSession().getLocalRepository().getBasedir();

        File file1 = new File(repoDir,
                "test/oj/resolve/a/1.2.3/a-1.2.3.jar");
        File file2 = new File(repoDir,
                "test/oj/resolve/b/4.5.6/b-4.5.6.jar");
        File file3 = new File(repoDir,
                "test/oj/resolve/c/7.8.9/c-7.8.9.jar");

        assertThat(files, contains(file1, file2, file3));
    }

}
