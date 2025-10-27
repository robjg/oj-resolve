package org.oddjob.maven.settings;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.oddjob.maven.props.RepoSessionProperties;
import org.oddjob.maven.resolve.ResolverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

/**
 * Builder Maven Settings. Much of this logic was copied from {@link org.apache.maven.resolver.internal.ant.AntRepoSys}.
 */
public class SettingsBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SettingsBuilder.class);

    private static final org.apache.maven.settings.building.SettingsBuilder SETTINGS_BUILDER =
            new DefaultSettingsBuilderFactory().newInstance();

    private static final SettingsDecrypter SETTINGS_DECRYPTOR =
            new AntSettingsDecryptorFactory().newInstance();

    private final RepoSessionProperties sessionProperties;

    private File userSettings;

    private File globalSettings;

    private boolean noDefaultUserSettings;

    private boolean noDefaultGlobalSettings;

    private SettingsBuilder(RepoSessionProperties sessionProperties) {
        this.sessionProperties = Objects.requireNonNull(sessionProperties);
    }

    public static SettingsBuilder from(RepoSessionProperties sessionProperties) {
        return  new SettingsBuilder(sessionProperties);
    }

    public SettingsBuilder withUserSettings(File userSettings) {
        this.userSettings = userSettings;
        return this;
    }

    public SettingsBuilder withNoDefaultUserSettings(boolean noDefaultUserSettings) {
        this.noDefaultUserSettings = noDefaultUserSettings;
        return this;
    }

    public SettingsBuilder withGlobalSettings(File globalSettings) {
        this.globalSettings = globalSettings;
        return this;
    }

    public SettingsBuilder withNoDefaultGlobalSettings(boolean noDefaultUserSettings) {
        this.noDefaultGlobalSettings = noDefaultUserSettings;
        return this;
    }

    protected File getUserSettings() {
        File userSettings = Optional.ofNullable(this.userSettings)
                .orElseGet(() ->
                        noDefaultUserSettings ? null : ResolverUtils.findUserSettings(sessionProperties));
        if (userSettings == null) {
            return null;
        }
        else if (userSettings.isFile()) {
            return userSettings;
        }
        else {
            logger.info("User settings file {} does not exist.", userSettings);
            return null;
        }
    }

    protected File getGlobalSettings() {
        File globalSettings = Optional.ofNullable(this.globalSettings)
                .orElseGet(() ->
                        noDefaultGlobalSettings ? null : ResolverUtils.findGlobalSettings(sessionProperties));
        if (globalSettings == null) {
            return null;
        }
        else if (globalSettings.isFile()) {
            return globalSettings;
        }
        else {
            logger.info("Global settings file {} does not exist.", globalSettings);
            return null;
        }
    }

    public Settings build() throws SettingsBuildingException {

        File userSettings = getUserSettings();
        File globalSettings = getGlobalSettings();

        logger.info("Creating settings with User Settings file {} and Global Settings file {}.",
                userSettings == null ? "(none)" : userSettings.toString(),
                globalSettings == null ? "(none)" : globalSettings.toString());

        DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
        request.setUserSettingsFile(userSettings);
        request.setGlobalSettingsFile(globalSettings);
        request.setSystemProperties(sessionProperties.getSystemProperties());
        request.setUserProperties(sessionProperties.getUserProperties());

        Settings settings = SETTINGS_BUILDER.build( request ).getEffectiveSettings();

        SettingsDecryptionResult result =
                SETTINGS_DECRYPTOR.decrypt( new DefaultSettingsDecryptionRequest( settings ) );

        settings.setServers( result.getServers() );

        settings.setProxies( result.getProxies() );

        return settings;
    }

    public static String settingsToString(Settings settings) {
        if (settings == null) {
            return "null";
        }
        else {
            return "Settings:{ localRep: " + settings.getLocalRepository() +
                    ", mirrors " + settings.getMirrors().size() +
                    ", proxies " + settings.getProxies().size() +
                    ", servers " + settings.getServers().size() +
                    " }";
        }
    }

}
