package org.oddjob.resolve;

import org.oddjob.resolve.types.Dependencies;
import org.oddjob.resolve.types.Dependency;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.resolution.ArtifactResult;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;

public class DependencyResolverTest {

    @Test
    public void resolveWithDefaults() throws SettingsBuildingException {

        ArooaSession arooaSession = new StandardArooaSession();

        ResolverSessionProperties sessionProperties = new ArooaResolverProperties(
                arooaSession.getPropertyManager(), null);


        Settings settings = SettingsBuilder.from(sessionProperties)
                .buildSettings();

        ResolverSession resolverSession = ResolverSessionBuilder.from(sessionProperties)
                .withSettings(settings)
                .build();

        DependencyCollector dependencyCollector = DependencyCollectorBuilder.from(resolverSession)
                .withDefaultRepos()
                .build();

        Dependency dependency = new Dependency();
        dependency.setCoords("commons-beanutils:commons-beanutils:1.9.4");

        Dependencies dependencies = new Dependencies();
        dependencies.addDependency(dependency);

        CollectResult result = dependencyCollector.collectDependencies(dependencies);

        DependencyResolver dependencyResolver = DependencyResolverBuilder.from(resolverSession)
                .build();

        List<ArtifactResult> results = dependencyResolver.resolve(result.getRoot());

        List<File> files = results.stream()
                .map(ar -> ar.getArtifact().getFile())
                .collect(Collectors.toList());

        File repoDir = resolverSession.getSession().getLocalRepository().getBasedir();

        File file1 = new File(repoDir,
                "commons-beanutils/commons-beanutils/1.9.4/commons-beanutils-1.9.4.jar");
        File file2 = new File(repoDir,
                "commons-logging/commons-logging/1.2/commons-logging-1.2.jar");
        File file3 = new File(repoDir,
                "commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar");

        assertThat(files, Matchers.contains(file1, file2, file3));

    }

}