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

/**
 */
public class Proxy
{

    private String host;

    private int port;

    private String protocol;

    private String nonProxyHosts;

    private Authentication authentication;


    public String getHost()
    {
        return host;
    }

    public void setHost( String host )
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort( int port )
    {
        if ( port <= 0 || port > 0xFFFF )
        {
            throw new IllegalArgumentException( "The port number must be within the range 1 - 65535" );
        }
        this.port = port;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getNonProxyHosts()
    {
        return nonProxyHosts;
    }

    public void setNonProxyHosts( String nonProxyHosts )
    {
        this.nonProxyHosts = nonProxyHosts;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication( Authentication authentication )
    {
        this.authentication = authentication;
    }

}
