package org.oddjob.maven.util;


import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.*;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.oddjob.maven.types.*;

import java.util.*;

/**
 * Utility methods to convert between Aether and Oddjob objects.
 *
 * Based on {@code org.apache.maven.resolver.internal.ant.ConverterUtils}.
 */
public class ConverterUtils
{

    private static org.eclipse.aether.artifact.Artifact toArtifact(Dependency dependency, ArtifactTypeRegistry types )
    {
        ArtifactType type = types.get( dependency.getType() );
        if ( type == null )
        {
            type = new DefaultArtifactType( dependency.getType() );
        }

        Map<String, String> props = null;
        if ( "system".equals( dependency.getScope() ) && dependency.getSystemPath() != null )
        {
            props = Collections.singletonMap( ArtifactProperties.LOCAL_PATH, dependency.getSystemPath().getPath() );
        }

        return new DefaultArtifact( dependency.getGroupId(), dependency.getArtifactId(), dependency.getClassifier(), null,
                             dependency.getVersion(), props, type );
    }

    public static org.eclipse.aether.repository.Authentication toAuthentication( Authentication auth )
    {
        if ( auth == null )
        {
            return null;
        }
        AuthenticationBuilder authBuilder = new AuthenticationBuilder();
        authBuilder.addUsername( auth.getUsername() ).addPassword( auth.getPassword() );
        authBuilder.addPrivateKey( auth.getPrivateKeyFile(), auth.getPassphrase() );
        return authBuilder.build();
    }

    public static org.eclipse.aether.graph.Dependency toDependency( Dependency dependency, List<Exclusion> exclusions,
                                                                     RepositorySystemSession session )
    {
        return new org.eclipse.aether.graph.Dependency( toArtifact( dependency, session.getArtifactTypeRegistry() ),
                                                         dependency.getScope(), false,
                                                         toExclusions( dependency.getExclusions(), exclusions ) );
    }


    private static org.eclipse.aether.graph.Exclusion toExclusion( Exclusion exclusion )
    {
        return new org.eclipse.aether.graph.Exclusion( exclusion.getGroupId(), exclusion.getArtifactId(),
                                                        exclusion.getClassifier(), exclusion.getExtension() );
    }

    private static Collection<org.eclipse.aether.graph.Exclusion> toExclusions( Collection<Exclusion> exclusions1,
                                                                                 Collection<Exclusion> exclusions2 )
    {
        Collection<org.eclipse.aether.graph.Exclusion> results =
                new LinkedHashSet<>();
        if ( exclusions1 != null )
        {
            for ( Exclusion exclusion : exclusions1 )
            {
                results.add( toExclusion( exclusion ) );
            }
        }
        if ( exclusions2 != null )
        {
            for ( Exclusion exclusion : exclusions2 )
            {
                results.add( toExclusion( exclusion ) );
            }
        }
        return results;
    }

    private static RepositoryPolicy toPolicy(RemoteRepository.Policy policy, boolean enabled, String updates,
                                             String checksums )
    {
        if ( policy != null )
        {
            enabled = policy.isEnabled();
            if ( policy.getChecksums() != null )
            {
                checksums = policy.getChecksums();
            }
            if ( policy.getUpdates() != null )
            {
                updates = policy.getUpdates();
            }
        }
        return new RepositoryPolicy( enabled, updates, checksums );
    }

    public static org.eclipse.aether.repository.Proxy toProxy( Proxy proxy )
    {
        if ( proxy == null )
        {
            return null;
        }
        return new org.eclipse.aether.repository.Proxy( proxy.getProtocol(), proxy.getHost(), proxy.getPort(),
                                                         toAuthentication( proxy.getAuthentication() ) );
    }

    public static org.eclipse.aether.repository.RemoteRepository toRepository( RemoteRepository repo )
    {
        org.eclipse.aether.repository.RemoteRepository.Builder builder =
            new org.eclipse.aether.repository.RemoteRepository.Builder( repo.getId(), repo.getType(), repo.getUrl() );
        builder.setSnapshotPolicy( toPolicy( repo.getSnapshotPolicy(), repo.isSnapshots(), repo.getUpdates(),
                                             repo.getChecksums() ) );
        builder.setReleasePolicy( toPolicy( repo.getReleasePolicy(), repo.isReleases(), repo.getUpdates(),
                                            repo.getChecksums() ) );
        builder.setAuthentication( toAuthentication( repo.getAuthentication() ) );
        return builder.build();
    }


}
