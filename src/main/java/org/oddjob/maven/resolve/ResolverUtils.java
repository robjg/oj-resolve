package org.oddjob.maven.resolve;

import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.util.filter.ScopeDependencyFilter;
import org.oddjob.maven.props.RepoSessionProperties;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

/**
 * Copied from {@code  org.apache.maven.resolver.internal.ant.AetherUtils}.
 */
public class ResolverUtils {

    public static final String SETTINGS_XML = "settings.xml";

    public static File findUserSettings(RepoSessionProperties project) {
        File userHome = new File(project.getProperty("user.home"));
        return new File(new File(userHome, ".m2"), SETTINGS_XML);
    }

    public static File findGlobalSettings(RepoSessionProperties project) {

        String mavenHome = getMavenHome(project);
        if (mavenHome == null) {
            return null;
        }
        else {
            return new File(new File(mavenHome, "conf"), SETTINGS_XML);
        }
    }

    public static String getMavenHome(RepoSessionProperties project) {

        String mavenHome = project.getProperty("maven.home");
        if (mavenHome != null) {
            return mavenHome;
        }
        return System.getenv("M2_HOME");
    }

    public static DependencyFilter createScopeFilter(String scopes) {
        Collection<String> included = new HashSet<>();
        Collection<String> excluded = new HashSet<>();

        String[] split = scopes.split("[, ]");
        for (String scope : split) {
            scope = scope.trim();
            Collection<String> dst;
            if (scope.startsWith("-") || scope.startsWith("!")) {
                dst = excluded;
                scope = scope.substring(1);
            } else {
                dst = included;
            }
            if (scope.length() > 0) {
                dst.add(scope);
            }
        }

        return new ScopeDependencyFilter(included, excluded);
    }
}
