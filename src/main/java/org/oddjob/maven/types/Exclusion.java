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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class Exclusion
{

    private static final String WILDCARD = "*";

    private String groupId;

    private String artifactId;

    private String classifier;

    private String extension;

    public void validate()
    {
            if ( groupId == null && artifactId == null && classifier == null && extension == null )
            {
                throw new ResolveException( "You must specify at least one of "
                    + "'groupId', 'artifactId', 'classifier' or 'extension'" );
            }
    }

    public String getGroupId()
    {
        return ( groupId != null ) ? groupId : WILDCARD;
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
        return ( artifactId != null ) ? artifactId : WILDCARD;
    }

    public void setArtifactId( String artifactId )
    {
        if ( this.artifactId != null )
        {
            throw ambiguousCoords();
        }
        this.artifactId = artifactId;
    }

    public String getClassifier()
    {
        return ( classifier != null ) ? classifier : WILDCARD;
    }

    public void setClassifier( String classifier )
    {
        if ( this.classifier != null )
        {
            throw ambiguousCoords();
        }
        this.classifier = classifier;
    }

    public String getExtension()
    {
        return ( extension != null ) ? extension : WILDCARD;
    }

    public void setExtension( String extension )
    {
        if ( this.extension != null )
        {
            throw ambiguousCoords();
        }
        this.extension = extension;
    }

    public void setCoords( String coords )
    {
        if ( groupId != null || artifactId != null || extension != null || classifier != null )
        {
            throw ambiguousCoords();
        }
        Pattern p = Pattern.compile( "([^: ]+)(:([^: ]+)(:([^: ]+)(:([^: ]*))?)?)?" );
        Matcher m = p.matcher( coords );
        if ( !m.matches() )
        {
            throw new ResolveException( "Bad exclusion coordinates '" + coords
                + "', expected format is <groupId>[:<artifactId>[:<extension>[:<classifier>]]]" );
        }
        groupId = m.group( 1 );
        artifactId = m.group( 3 );
        if ( artifactId == null )
        {
            artifactId = "*";
        }
        extension = m.group( 5 );
        if ( extension == null )
        {
            extension = "*";
        }
        classifier = m.group( 7 );
        if ( classifier == null )
        {
            classifier = "*";
        }
    }

    private ResolveException ambiguousCoords()
    {
        return new ResolveException( "You must not specify both 'coords' and "
            + "('groupId', 'artifactId', 'extension', 'classifier')" );
    }

}
