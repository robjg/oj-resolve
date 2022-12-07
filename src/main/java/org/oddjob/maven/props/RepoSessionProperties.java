package org.oddjob.maven.props;

import java.util.Properties;

/**
 * Provides properties used by Maven.
 */
public interface RepoSessionProperties {

    String getProperty(String name);

    Properties getSystemProperties();

    Properties getUserProperties();

    Properties getAllProperties();
}
