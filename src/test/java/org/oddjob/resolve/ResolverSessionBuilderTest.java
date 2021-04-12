package org.oddjob.resolve;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResolverSessionBuilderTest {

    @Test
    public void testResolverSessionBuilt() throws SettingsBuildingException {

        ArooaSession arooaSession = new StandardArooaSession();

        ResolverSessionProperties sessionProperties = new ArooaResolverProperties(
                arooaSession.getPropertyManager(), null);


        Settings settings = SettingsBuilder.from(sessionProperties)
                .buildSettings();

        ResolverSession resolverSession = ResolverSessionBuilder.from(sessionProperties)
                .withSettings(settings)
                .build();

        assertThat(resolverSession.getSession().getLocalRepositoryManager().getRepository().getBasedir(),
                notNullValue());

        assertThat(resolverSession.getSystem(),
                notNullValue());
    }
}