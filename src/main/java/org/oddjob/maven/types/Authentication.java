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

import java.util.ArrayList;
import java.util.List;

/**
 */
public class Authentication
{

    private String username;

    private String password;

    private String privateKeyFile;

    private String passphrase;

    private List<String> servers = new ArrayList<String>();

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getPrivateKeyFile()
    {
        return privateKeyFile;
    }

    public void setPrivateKeyFile( String privateKeyFile )
    {
        this.privateKeyFile = privateKeyFile;
    }

    public String getPassphrase()
    {
        return passphrase;
    }

    public void setPassphrase( String passphrase )
    {
        this.passphrase = passphrase;
    }

    public List<String> getServers()
    {
        return servers;
    }

    public void setServers( String servers )
    {
        this.servers.clear();
        String[] split = servers.split( "[;:]" );
        for ( String server : split )
        {
            server = server.trim();
            if ( server.length() > 0 )
            {
                this.servers.add( server );
            }
        }
    }

}
