package org.oddjob;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Eclipse puts test classes on the classpath, Intellij doesn't. The allows us to run Oddjob with the test
 * classpath from intellij.
 */
public class MainForIntellij {

   @Ignore
    @Test
    public void fakeTestToCreateClassPath() throws Exception {
        Main.main(new String[0]);
    }
}
