package org.oddjob.resolve.types;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.oddjob.resolve.ResolveException;
import org.eclipse.aether.repository.RepositoryPolicy;

import java.util.Collections;
import java.util.List;

/**
 */
public class RemoteRepository
    implements RemoteRepositoryContainer
{

    private String id;

    private String url;

    private String type;

    private Policy releasePolicy;

    private Policy snapshotPolicy;

    private boolean releases = true;

    private boolean snapshots = false;

    private String checksums;

    private String updates;

    private Authentication authentication;


    public void validate()
    {
            if ( url == null || url.length() <= 0 )
            {
                throw new ResolveException( "You must specify the 'url' for a remote repository" );
            }
            if ( id == null || id.length() <= 0 )
            {
                throw new ResolveException( "You must specify the 'id' for a remote repository" );
            }
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getType()
    {
        return ( type != null ) ? type : "default";
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public Policy getReleasePolicy()
    {
        return releasePolicy;
    }

    public void setReleases(Policy policy )
    {
        this.releasePolicy = policy;
    }

    public Policy getSnapshotPolicy()
    {
        return snapshotPolicy;
    }

    public void setSnapshots(Policy policy )
    {
        this.snapshotPolicy = policy;
    }

    public boolean isReleases()
    {
        return releases;
    }

    public void setReleases( boolean releases )
    {
        this.releases = releases;
    }

    public boolean isSnapshots()
    {
        return snapshots;
    }

    public void setSnapshots( boolean snapshots )
    {
        this.snapshots = snapshots;
    }

    public String getUpdates()
    {
        return ( updates != null ) ? updates : RepositoryPolicy.UPDATE_POLICY_DAILY;
    }

    public void setUpdates( String updates )
    {
        checkUpdates( updates );
        this.updates = updates;
    }

    protected static void checkUpdates( String updates )
    {
        if ( !RepositoryPolicy.UPDATE_POLICY_ALWAYS.equals( updates )
            && !RepositoryPolicy.UPDATE_POLICY_DAILY.equals( updates )
            && !RepositoryPolicy.UPDATE_POLICY_NEVER.equals( updates )
            && !updates.startsWith( RepositoryPolicy.UPDATE_POLICY_INTERVAL ) )
        {
            throw new ResolveException( "'" + updates + "' is not a permitted update policy" );
        }
    }

    public String getChecksums()
    {
        return ( checksums != null ) ? checksums : RepositoryPolicy.CHECKSUM_POLICY_WARN;
    }

    public void setChecksums( String checksums )
    {
        checkChecksums( checksums );
        this.checksums = checksums;
    }

    protected static void checkChecksums( String checksums )
    {
        if ( !RepositoryPolicy.CHECKSUM_POLICY_FAIL.equals( checksums )
            && !RepositoryPolicy.CHECKSUM_POLICY_WARN.equals( checksums )
            && !RepositoryPolicy.CHECKSUM_POLICY_IGNORE.equals( checksums ) )
        {
            throw new ResolveException( "'" + checksums + "' is not a permitted checksum policy" );
        }
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication( Authentication authentication )
    {
        this.authentication = authentication;
    }

    public List<RemoteRepository> getRepositories()
    {
        return Collections.singletonList( this );
    }

    /**
     */
    public static class Policy
    {

        private boolean enabled = true;

        private String checksumPolicy;

        private String updatePolicy;

        public boolean isEnabled()
        {
            return enabled;
        }

        public void setEnabled( boolean enabled )
        {
            this.enabled = enabled;
        }

        public String getChecksums()
        {
            return checksumPolicy;
        }

        public void setChecksums( String checksumPolicy )
        {
            checkChecksums( checksumPolicy );
            this.checksumPolicy = checksumPolicy;
        }

        public String getUpdates()
        {
            return updatePolicy;
        }

        public void setUpdates( String updatePolicy )
        {
            checkUpdates( updatePolicy );
            this.updatePolicy = updatePolicy;
        }

    }

}
