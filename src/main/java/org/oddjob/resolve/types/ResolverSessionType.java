package org.oddjob.resolve.types;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.resolve.*;

import java.io.File;
import java.util.*;

public class ResolverSessionType implements ValueFactory<ResolverSession>, ArooaSessionAware {

    private Properties userProperties;

    private File userSettings;

    private File globalSettings;

    private File localRepository;

    private final List<Mirror> mirrors = new ArrayList<>();

    private final List<Proxy> proxies = new ArrayList<>();

    private final List<Authentication> authentications = new ArrayList<>();

    private ArooaSession arooaSession;

    @Override
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }

    @Override
    public ResolverSession toValue() throws ArooaConversionException {
        ArooaSession arooaSession = Objects.requireNonNull(this.arooaSession);

        ResolverSessionProperties sessionProperties = new ArooaResolverProperties(
                arooaSession.getPropertyManager(), this.userProperties);

        Settings settings;
        try {
            settings = SettingsBuilder.from(sessionProperties)
                    .withUserSettings(userSettings)
                    .withGlobalSettings(globalSettings)
                    .buildSettings();

            return ResolverSessionBuilder.from(sessionProperties)
                    .withSettings(settings)
                    .withLocalRepo(localRepository)
                    .withMirrors(mirrors)
                    .withProxies(proxies)
                    .withAuthentications(authentications)
                    .build();

        } catch (SettingsBuildingException e) {
            throw new ArooaConversionException(e);
        }
    }

    public void setUserProperties(Properties userProperties) {
        this.userProperties = userProperties;
    }

    public void setUserSettings(File userSettings) {
        this.userSettings = userSettings;
    }

    public void setGlobalSettings(File globalSettings) {
        this.globalSettings = globalSettings;
    }

    public void setLocalRepository(File localRepository) {
        this.localRepository = localRepository;
    }

    public void setMirrors(int index, Mirror mirror) {
        new ListSetterHelper<>(this.mirrors).set(index, mirror);
    }

    public void setProxies(int index, Proxy proxy) {
        new ListSetterHelper<>(this.proxies).set(index, proxy);
    }

    public void setAuthentications(int index, Authentication authentication) {
        new ListSetterHelper<>(this.authentications).set(index, authentication);
    }

}
