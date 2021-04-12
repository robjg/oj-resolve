package org.oddjob.resolve;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class SettingsBuilderTest {

    @Test
    public void testBuild() throws SettingsBuildingException {

        ArooaSession arooaSession = new StandardArooaSession();

        ResolverSessionProperties sessionProperties = new ArooaResolverProperties(
                arooaSession.getPropertyManager(), null);

        Settings settings = SettingsBuilder.from(sessionProperties)
                .withUserSettings(new File(getClass().getResource("/oddjob/Session/settings.xml").getFile()))
                .buildSettings();

        assertThat(settings.getLocalRepository(), notNullValue());
    }
}