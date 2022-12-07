package org.oddjob.maven.props;


import java.util.Properties;

/**
 * Both System and User Properties set by us.
 */
public class BespokeRepoSessionProperties implements RepoSessionProperties {

    private final Properties systemProperties;

    private final Properties userProperties;

    private final Properties allProperties = new Properties();

    private BespokeRepoSessionProperties(Properties systemProperties, Properties userProperties) {

        this.systemProperties = systemProperties == null ? new Properties() : systemProperties;
        this.userProperties = userProperties == null ? new Properties() : userProperties;
        this.allProperties.putAll(this.systemProperties);
        this.allProperties.putAll(this.userProperties);
    }

    public static RepoSessionProperties fromSystemAndUserProperties(Properties systemProperties, Properties userProperties) {
        return new BespokeRepoSessionProperties(systemProperties, userProperties);
    }

    public static RepoSessionProperties fromSystemProperties(Properties systemProperties) {
        return new BespokeRepoSessionProperties(systemProperties, null);
    }

    public static RepoSessionProperties fromUserProperties(Properties userProperties) {
        return new BespokeRepoSessionProperties(null, userProperties);
    }

    public static RepoSessionProperties empty() {
        return new BespokeRepoSessionProperties(null, null);
    }

    @Override
    public String getProperty(String name) {
        return allProperties.getProperty(name);
    }

    @Override
    public Properties getSystemProperties() {
        return systemProperties;
    }

    @Override
    public Properties getUserProperties() {
        return userProperties;
    }

    @Override
    public Properties getAllProperties() {
        return allProperties;
    }
}
