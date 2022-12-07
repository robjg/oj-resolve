package org.oddjob.maven.settings;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.junit.jupiter.api.Test;
import org.oddjob.maven.props.BespokeRepoSessionProperties;
import org.oddjob.maven.props.RepoSessionProperties;

import java.io.File;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SettingsBuilderTest {

    @Test
    void testUserSettings() throws SettingsBuildingException {

        File userSettings = new File(Objects.requireNonNull(
                        getClass().getResource("/settings/user/our-settings.xml"))
                .getFile());

        RepoSessionProperties props = BespokeRepoSessionProperties.empty();

        Settings settings = SettingsBuilder.from(props)
                .withUserSettings(userSettings)
                .withNoDefaultGlobalSettings(true)
                .build();

        assertThat(settings.getLocalRepository(), is("repo-from-our-user-settings"));

        assertThat(SettingsBuilder.settingsToString(settings), is(
                "Settings:{ localRep: repo-from-our-user-settings, mirrors 0, proxies 0, servers 0 }"));
    }

    @Test
    void testDefaultUserSettings() throws SettingsBuildingException {

        Properties properties = new Properties();
        properties.setProperty("user.home", new File(
                Objects.requireNonNull(getClass().getResource("/settings/user/our-settings.xml"))
                        .getFile()).getParent());

        RepoSessionProperties props = BespokeRepoSessionProperties.fromSystemProperties(properties);

        Settings settings = SettingsBuilder.from(props)
                .withNoDefaultGlobalSettings(true)
                .build();

        assertThat(settings.getLocalRepository(), is("repo-from-default-user-settings"));
    }

    @Test
    void testGlobalSettings() throws SettingsBuildingException {

        File globalSettings = new File(Objects.requireNonNull(getClass().getResource("/settings/global/our-settings.xml"))
                .getFile());

        RepoSessionProperties props = BespokeRepoSessionProperties.empty();

        Settings settings = SettingsBuilder.from(props)
                .withGlobalSettings(globalSettings)
                .withNoDefaultUserSettings(true)
                .build();

        assertThat(settings.getLocalRepository(), is("repo-from-our-global-settings"));
    }

    @Test
    void testDefaultGlobalSettings() throws SettingsBuildingException {

        Properties properties = new Properties();
        properties.setProperty("maven.home", new File(
                Objects.requireNonNull(getClass().getResource("/settings/global/our-settings.xml"))
                        .getFile()).getParent());

        RepoSessionProperties props = BespokeRepoSessionProperties.fromSystemProperties(properties);

        Settings settings = SettingsBuilder.from(props)
                .withNoDefaultUserSettings(true)
                .build();

        assertThat(settings.getLocalRepository(), is("repo-from-default-global-settings"));
    }

    @Test
    void testBothSettings() throws SettingsBuildingException {

        File globalSettings = new File(Objects.requireNonNull(
                getClass().getResource("/settings/global/our-settings.xml")).getFile());

        File userSettings = new File(Objects.requireNonNull(
                        getClass().getResource("/settings/user/our-settings.xml")).getFile());

        RepoSessionProperties props = BespokeRepoSessionProperties.empty();

        Settings settings = SettingsBuilder.from(props)
                .withUserSettings(userSettings)
                .withGlobalSettings(globalSettings)
                .build();

        assertThat(SettingsBuilder.settingsToString(settings), is(
                "Settings:{ localRep: repo-from-our-user-settings, mirrors 1, proxies 0, servers 0 }"));
    }

    @Test
    void testNoSettings() throws SettingsBuildingException {
        Properties properties = new Properties();
        properties.setProperty("user.home", new File(
                Objects.requireNonNull(getClass().getResource("/settings/user/our-settings.xml"))
                        .getFile()).getParent());

        RepoSessionProperties props = BespokeRepoSessionProperties.fromSystemProperties(properties);

        Settings settings = SettingsBuilder.from(props)
                .withNoDefaultUserSettings(true)
                .withNoDefaultGlobalSettings(true)
                .build();

        assertThat(settings.getLocalRepository(), nullValue());
    }

    @Test
    void testUserAndGlobalDefaultSettings() throws SettingsBuildingException {

        Properties properties = new Properties();
        properties.setProperty("user.home", new File(
                Objects.requireNonNull(getClass().getResource("/settings/user/our-settings.xml"))
                        .getFile()).getParent());
        properties.setProperty("maven.home", new File(
                Objects.requireNonNull(getClass().getResource("/settings/global/our-settings.xml"))
                        .getFile()).getParent());

        RepoSessionProperties props = BespokeRepoSessionProperties.fromSystemProperties(properties);

        Settings settings = SettingsBuilder.from(props)
                .build();

        assertThat(SettingsBuilder.settingsToString(settings), is(
                "Settings:{ localRep: repo-from-default-user-settings, mirrors 0, proxies 0, servers 1 }"));
    }

}