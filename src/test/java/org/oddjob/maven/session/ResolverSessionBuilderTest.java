package org.oddjob.maven.session;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.maven.props.ArooaRepoProperties;
import org.oddjob.maven.props.RepoSessionProperties;
import org.oddjob.maven.settings.SettingsBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class ResolverSessionBuilderTest {

    @Test
    public void testResolverSessionBuilt() throws SettingsBuildingException {

        ArooaSession arooaSession = new StandardArooaSession();

        RepoSessionProperties sessionProperties = ArooaRepoProperties.from(
                arooaSession.getPropertyManager());

        Settings settings = SettingsBuilder.from(sessionProperties)
                .build();

        ResolverSession resolverSession = ResolverSessionBuilder.from(sessionProperties)
                .withSettings(settings)
                .build();

        assertThat(resolverSession.getSession().getLocalRepositoryManager().getRepository().getBasedir(),
                notNullValue());

        assertThat(resolverSession.getSystem(),
                notNullValue());
    }
}