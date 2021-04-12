package org.oddjob.resolve;

import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.util.filter.ScopeDependencyFilter;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class ResolverUtils {

    public static final String SETTINGS_XML = "settings.xml";

    public static File findUserSettings(ResolverSessionProperties project) {
        File userHome = new File(project.getProperty("user.home"));
        File file = new File(new File(userHome, ".ant"), SETTINGS_XML);
        if (file.isFile()) {
            return file;
        } else {
            return new File(new File(userHome, ".m2"), SETTINGS_XML);
        }
    }

    public static File findGlobalSettings(ResolverSessionProperties project) {

        File file = new File(new File(project.getProperty("ant.home"), "etc"), SETTINGS_XML);
        if (file.isFile()) {
            return file;
        } else {
            String mavenHome = getMavenHome(project);
            if (mavenHome != null) {
                return new File(new File(mavenHome, "conf"), SETTINGS_XML);
            }
        }

        return null;
    }

    public static String getMavenHome(ResolverSessionProperties project) {

        String mavenHome = project.getProperty("maven.home");
        if (mavenHome != null) {
            return mavenHome;
        }
        return System.getenv("M2_HOME");
    }

    public static DependencyFilter createScopeFilter(String scopes) {
        Collection<String> included = new HashSet<>();
        Collection<String> excluded = new HashSet<>();

        String[] split = scopes.split( "[, ]" );
        for ( String scope : split )
        {
            scope = scope.trim();
            Collection<String> dst;
            if ( scope.startsWith( "-" ) || scope.startsWith( "!" ) )
            {
                dst = excluded;
                scope = scope.substring( 1 );
            }
            else
            {
                dst = included;
            }
            if ( scope.length() > 0 )
            {
                dst.add( scope );
            }
        }

        return new ScopeDependencyFilter( included, excluded );
    }
}
