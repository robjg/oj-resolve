package org.oddjob.resolve;

import org.oddjob.arooa.runtime.PropertyLookup;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class ArooaResolverProperties implements ResolverSessionProperties {

    static final boolean OS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    private final Properties sessionProperties;

    private final Properties userProperties;

    private final Properties allProperties;

    public ArooaResolverProperties(PropertyLookup sessionProperties, Properties userProperties) {
        this.sessionProperties = getEnvProperties();
        for (String name : sessionProperties.propertyNames()) {
            this.sessionProperties.setProperty(name, sessionProperties.lookup(name));
        }
        this.userProperties = userProperties;
        this.allProperties = new Properties(this.sessionProperties);
        Optional.ofNullable(userProperties).ifPresent(this.allProperties::putAll);
    }

    @Override
    public String apply(String name) {
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
