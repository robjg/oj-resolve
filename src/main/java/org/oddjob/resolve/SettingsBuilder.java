package org.oddjob.resolve;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;

import java.io.File;
import java.util.*;

public class SettingsBuilder {

    private static final org.apache.maven.settings.building.SettingsBuilder SETTINGS_BUILDER = new DefaultSettingsBuilderFactory().newInstance();

    private static final SettingsDecrypter SETTINGS_DECRYPTER = new AntSettingsDecryptorFactory().newInstance();

    private final ResolverSessionProperties sessionProperties;

    private File userSettings;

    private File globalSettings;

    private SettingsBuilder(ResolverSessionProperties sessionProperties) {
        this.sessionProperties = Objects.requireNonNull(sessionProperties);
    }

    public static SettingsBuilder from(ResolverSessionProperties sessionProperties) {
        return  new SettingsBuilder(sessionProperties);
    }

    public SettingsBuilder withUserSettings(File userSettings) {
        this.userSettings = userSettings;
        return this;
    }

    public SettingsBuilder withGlobalSettings(File globalSettings) {
        this.globalSettings = globalSettings;
        return this;
    }

    public Settings buildSettings() throws SettingsBuildingException {

        File userSettings = Optional.ofNullable(this.userSettings)
                .orElseGet(() -> ResolverUtils.findUserSettings(sessionProperties));

        File globalSettings = Optional.ofNullable(this.globalSettings)
                .orElseGet(() -> ResolverUtils.findGlobalSettings(sessionProperties));

        DefaultSettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
        request.setUserSettingsFile( userSettings );
        request.setGlobalSettingsFile( globalSettings );
        request.setSystemProperties( sessionProperties.getSystemProperties() );
        request.setUserProperties( sessionProperties.getUserProperties() );

        Settings settings = SETTINGS_BUILDER.build( request ).getEffectiveSettings();

        SettingsDecryptionResult result =
                SETTINGS_DECRYPTER.decrypt( new DefaultSettingsDecryptionRequest( settings ) );

        settings.setServers( result.getServers() );

        settings.setProxies( result.getProxies() );

        return settings;
    }

}
