package org.oddjob.maven.types;

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

import org.oddjob.maven.resolve.ResolveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Dependency
    implements DependencyContainer
{
    private static final Logger logger = LoggerFactory.getLogger(Dependency.class);

    private String groupId;

    private String artifactId;

    private String version;

    private String classifier;

    private String type;

    private String scope;

    private File systemPath;

    private final List<Exclusion> exclusions = new ArrayList<Exclusion>();


    public void validate()
    {
            if ( groupId == null || groupId.length() <= 0 )
            {
                throw new ResolveException( "You must specify the 'groupId' for a dependency" );
            }
            if ( artifactId == null || artifactId.length() <= 0 )
            {
                throw new ResolveException( "You must specify the 'artifactId' for a dependency" );
            }
            if ( version == null || version.length() <= 0 )
            {
                throw new ResolveException( "You must specify the 'version' for a dependency" );
            }

            if ( "system".equals( scope ) )
            {
                if ( systemPath == null )
                {
                    throw new ResolveException( "You must specify 'systemPath' for dependencies with scope=system" );
                }
            }
            else if ( systemPath != null )
            {
                throw new ResolveException( "You may only specify 'systemPath' for dependencies with scope=system" );
            }

            if ( scope != null && !"compile".equals( scope ) && !"provided".equals( scope ) && !"system".equals( scope )
                && !"runtime".equals( scope ) && !"test".equals( scope ) )
            {
                logger.warn( "Unknown scope '" + scope + "' for dependency");
            }

            for ( Exclusion exclusion : exclusions )
            {
                exclusion.validate();
            }
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId( String groupId )
    {
        if ( this.groupId != null )
        {
            throw ambiguousCoords();
        }
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId( String artifactId )
    {
        if ( this.artifactId != null )
        {
            throw ambiguousCoords();
        }
        this.artifactId = artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        if ( this.version != null )
        {
            throw ambiguousCoords();
        }
        this.version = version;
    }

    public String getClassifier()
    {
        return classifier;
    }

    public void setClassifier( String classifier )
    {
        if ( this.classifier != null )
        {
            throw ambiguousCoords();
        }
        this.classifier = classifier;
    }

    public String getType()
    {
        return ( type != null ) ? type : "jar";
    }

    public void setType( String type )
    {
        if ( this.type != null )
        {
            throw ambiguousCoords();
        }
        this.type = type;
    }

    public String getScope()
    {
        return ( scope != null ) ? scope : "compile";
    }

    public void setScope( String scope )
    {
        if ( this.scope != null )
        {
            throw ambiguousCoords();
        }
        this.scope = scope;
    }

    public void setCoords( String coords )
    {
        if ( groupId != null || artifactId != null || version != null || type != null || classifier != null
            || scope != null )
        {
            throw ambiguousCoords();
        }
        Pattern p = Pattern.compile( "([^: ]+):([^: ]+):([^: ]+)((:([^: ]+)(:([^: ]+))?)?:([^: ]+))?" );
        Matcher m = p.matcher( coords );
        if ( !m.matches() )
        {
            throw new ResolveException( "Bad dependency coordinates '" + coords
                + "', expected format is <groupId>:<artifactId>:<version>[[:<type>[:<classifier>]]:<scope>]" );
        }
        groupId = m.group( 1 );
        artifactId = m.group( 2 );
        version = m.group( 3 );
        type = m.group( 6 );
        if ( type == null || type.length() <= 0 )
        {
            type = "jar";
        }
        classifier = m.group( 8 );
        if ( classifier == null )
        {
            classifier = "";
        }
        scope = m.group( 9 );
    }

    public void setSystemPath( File systemPath )
    {
        this.systemPath = systemPath;
    }

    public File getSystemPath()
    {
        return systemPath;
    }

    public String getVersionlessKey()
    {
        StringBuilder key = new StringBuilder( 128 );
        if ( groupId != null )
        {
            key.append( groupId );
        }
        key.append( ':' );
        if ( artifactId != null )
        {
            key.append( artifactId );
        }
        key.append( ':' );
        key.append( ( type != null ) ? type : "jar" );
        if ( classifier != null && classifier.length() > 0 )
        {
            key.append( ':' );
            key.append( classifier );
        }
        return key.toString();
    }

    public void addExclusion( Exclusion exclusion )
    {
        this.exclusions.add( exclusion );
    }

    public List<Exclusion> getExclusions()
    {
        return exclusions;
    }

    private ResolveException ambiguousCoords()
    {
        return new ResolveException( "You must not specify both 'coords' and "
            + "('groupId', 'artifactId', 'version', 'extension', 'classifier', 'scope')" );
    }

}
