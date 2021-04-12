package org.oddjob.resolve;

import java.util.Properties;
import java.util.function.Function;

public interface ResolverSessionProperties extends Function<String, String> {

    default String getProperty(String name) {
        return apply(name);
    }

    Properties getSystemProperties();

    Properties getUserProperties();

    Properties getAllProperties();
}
