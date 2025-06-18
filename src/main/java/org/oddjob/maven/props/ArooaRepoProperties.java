package org.oddjob.maven.props;

import org.oddjob.arooa.runtime.PropertyLookup;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Implementation of {@link RepoSessionProperties} that takes the properties from an
 * Arooa {@link PropertyLookup}.
 */
public class ArooaRepoProperties implements RepoSessionProperties {

    static final boolean OS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    private final Properties sessionProperties;

    private final Properties userProperties;

    private final Properties allProperties;

    private ArooaRepoProperties(PropertyLookup sessionProperties, Properties userProperties) {
        this.sessionProperties = getEnvProperties();
        for (String name : sessionProperties.propertyNames()) {
            this.sessionProperties.setProperty(name, sessionProperties.lookup(name));
        }
        this.userProperties = new Properties();
        this.allProperties = new Properties(this.sessionProperties);
        if (userProperties != null) {
            this.userProperties.putAll(userProperties);
            this.allProperties.putAll(userProperties);
        }
    }

    public static RepoSessionProperties from(PropertyLookup sessionProperties, Properties userProperties) {
        return new ArooaRepoProperties(sessionProperties, userProperties);
    }

    public static RepoSessionProperties from(PropertyLookup sessionProperties) {
        return new ArooaRepoProperties(sessionProperties, null);
    }

    @Override
    public String getProperty(String name) {
        return Optional.ofNullable(userProperties)
                .map(up -> up.getProperty(name))
                .orElseGet(() -> sessionProperties.getProperty(name));
    }

    @Override
    public Properties getSystemProperties() {
        return sessionProperties;
    }

    @Override
    public Properties getUserProperties() {
        return userProperties;
    }

    @Override
    public Properties getAllProperties() {
        return allProperties;
    }

    @Override
    public String toString() {
        return "ResolverSessionProperties: session " + sessionProperties.size() +
                ", user " + userProperties.size();
    }

    static Properties getEnvProperties() {

        Properties props = new Properties();

        for ( Map.Entry<String, String> entry : System.getenv().entrySet() ) {
            String key = entry.getKey();
            if (OS_WINDOWS)
            {
                key = key.toUpperCase( Locale.ENGLISH );
            }
            key = "env." + key;
            props.put( key, entry.getValue() );
        }

        return props;
    }
}
