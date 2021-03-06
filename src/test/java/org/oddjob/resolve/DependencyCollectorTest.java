package org.oddjob.resolve;

import org.eclipse.aether.repository.RemoteRepository;
import org.oddjob.resolve.types.Dependency;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.graph.DependencyNode;
import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.net.MalformedURLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class DependencyCollectorTest {

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

        CollectResult result = dependencyCollector.collectDependencies(dependency);

        DependencyNode root = result.getRoot();


        assertThat(root.getArtifact(), nullValue());
        assertThat(root.getChildren().size(), is(1));

        DependencyNode child1 = root.getChildren().get(0);

        assertThat(child1.getArtifact().toString(), is("commons-beanutils:commons-beanutils:jar:1.9.4"));

        assertThat(child1.getChildren().size(), is(2));

        DependencyNode child11 = child1.getChildren().get(0);

        DependencyNode child12 = child1.getChildren().get(1);

        assertThat(child11.getArtifact().toString(), is("commons-logging:commons-logging:jar:1.2"));
        assertThat(child12.getArtifact().toString(), is("commons-collections:commons-collections:jar:3.2.2"));
    }

    @Test
    public void testGetRemoteRepositories() throws SettingsBuildingException {

        ArooaSession arooaSession = new StandardArooaSession();

        ResolverSessionProperties sessionProperties = new ArooaResolverProperties(
                arooaSession.getPropertyManager(), null);

        ResolverSession resolverSession = ResolverSessionBuilder.from(sessionProperties)
                .build();

        DependencyCollector dependencyCollector = DependencyCollectorBuilder.from(resolverSession)
                .build();

        List<RemoteRepository> repoList = dependencyCollector.getRemoteRepositories();

        assertThat(repoList.size(), is(0));
    }

    @Test
    public void testMergedRemoteRepositories() throws SettingsBuildingException {

        ArooaSession arooaSession = new StandardArooaSession();

        ResolverSessionProperties sessionProperties = new ArooaResolverProperties(
                arooaSession.getPropertyManager(), null);

        ResolverSession resolverSession = ResolverSessionBuilder.from(sessionProperties)
                .build();

        DependencyCollector dependencyCollector = DependencyCollectorBuilder.from(resolverSession)
                .withDefaultRepos()
                .build();

        List<org.eclipse.aether.repository.RemoteRepository> repoList =
                dependencyCollector.getRemoteRepositories();

        assertThat(repoList.size(), is(1));

        org.eclipse.aether.repository.RemoteRepository repo = repoList.get(0);

        assertThat(repo.getUrl(), is("https://repo1.maven.org/maven2/"));
    }
}