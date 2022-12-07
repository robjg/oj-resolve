package org.oddjob.maven.ant;

import org.junit.Test;
import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.maven.OurDirs;
import org.oddjob.state.ParentState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;

public class AntResolveTest {

    /** Prove resolve with mirror works with Ant. */
    @Test
    public void antResolveWithMirror() throws IOException, FailedToStopException {

        Path work = OurDirs.workPathDir(
                getClass().getSimpleName() + "-antResolveWithMirror",
                true);

        File config = new File(Objects.requireNonNull(
                getClass().getResource("/ant/oj-ant-resolve-mirror.xml")).getFile());

        Properties properties = new Properties();
        properties.setProperty("work.dir", work.toAbsolutePath().toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(config);
        oddjob.setProperties(properties);

        Path repo = work.resolve( "repo");

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.STARTED));

        assertThat(repo.resolve("test/oj/resolve/a/1.2.3/a-1.2.3.jar").toFile(), anExistingFile());
        assertThat(repo.resolve("test/oj/resolve/b/4.5.6/b-4.5.6.jar").toFile(), anExistingFile());
        assertThat(repo.resolve("test/oj/resolve/c/7.8.9/c-7.8.9.jar").toFile(), anExistingFile());

        oddjob.stop();
        oddjob.destroy();
    }

}
