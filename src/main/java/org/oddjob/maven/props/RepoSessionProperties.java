package org.oddjob.maven.props;

import java.util.Properties;

/**
 * Provides properties used by Maven. In Ant these are provided by the {@code AntRepoSys}
 * which re-creates them every time they are accessed.
 */
public interface RepoSessionProperties {

    String getProperty(String name);

    Properties getSystemProperties();

    Properties getUserProperties();

    Properties getAllProperties();
}
