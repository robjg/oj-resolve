package org.oddjob.maven.types;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.maven.props.ArooaRepoProperties;
import org.oddjob.maven.props.RepoSessionProperties;
import org.oddjob.maven.session.ResolverSession;
import org.oddjob.maven.session.ResolverSessionBuilder;
import org.oddjob.maven.settings.SettingsBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @oddjob.description Provide a Session for resolving artifact from Maven. Allows settings
 * and other session properties to be overridden.
 *
 * @oddjob.example Specify a repository.
 * {@oddjob.xml.resource oddjob/Resolve/resolve-from-repo.xml#snippet1}
 *
 * @oddjob.example Specify a mirror.
 * {@oddjob.xml.resource oddjob/Resolve/resolve-with-mirror.xml#snippet1}
 *
 * @oddjob.example Authentication.
 * {@oddjob.xml.resource oddjob/Resolve/resolve-basic-authentication.xml#snippet1}
 *
 */
public class ResolverSessionType implements ValueFactory<ResolverSession>, ArooaSessionAware {

    /**
     * @oddjob.description Specify additional user properties to be set.
     * @oddjob.required No.
     */
    private Properties userProperties;

    /**
     * @oddjob.description Specify a user settings file to be used in the session.
     * @oddjob.required No.
     */
    private File userSettings;

    /**
     * @oddjob.description Specify a global settings file to be used in the session.
     * @oddjob.required No.
     */
    private File globalSettings;

    /**
     * @oddjob.description Specify a local repository to be used in the session.
     * @oddjob.required No.
     */
    private File localRepository;

    /**
     * @oddjob.description Specify mirrors to be used in the session.
     * @oddjob.required No.
     */
    private final List<Mirror> mirrors = new ArrayList<>();

    /**
     * @oddjob.description Specify proxies to be used in the session.
     * @oddjob.required No.
     */
    private final List<Proxy> proxies = new ArrayList<>();

    /**
     * @oddjob.description Specify authentications to be used in the session.
     * @oddjob.required No.
     */
    private final List<Authentication> authentications = new ArrayList<>();

    private ArooaSession arooaSession;

    @Override
    @ArooaHidden
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }

    @Override
    public ResolverSession toValue() throws ArooaConversionException {
        ArooaSession arooaSession = Objects.requireNonNull(this.arooaSession);

        RepoSessionProperties sessionProperties = ArooaRepoProperties.from(
                arooaSession.getPropertyManager(), this.userProperties);

        Settings settings;
        try {
            settings = SettingsBuilder.from(sessionProperties)
                    .withUserSettings(userSettings)
                    .withGlobalSettings(globalSettings)
                    .build();

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
