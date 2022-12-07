package org.oddjob.maven.resolve;

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

import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.maven.jobs.ResolveJob;
import org.oddjob.maven.session.ResolverSession;
import org.oddjob.maven.types.Dependencies;
import org.oddjob.maven.types.Dependency;
import org.oddjob.state.ParentState;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class ResolveJobTest
{
    @Test
    public void testResolveWithDefaults() throws ArooaConversionException {

        Dependency dependency = new Dependency();
        dependency.setCoords("commons-beanutils:commons-beanutils:1.9.4");

        Dependencies dependencies = new Dependencies();
        dependencies.addDependency(dependency);

        ArooaSession session = new StandardArooaSession();
        ResolveJob resolve = new ResolveJob();

        resolve.setArooaSession(session);
        resolve.setDependencies(dependencies);

        resolve.run();

        List<File> files = resolve.getResolvedFiles();;

        File repoDir = resolve.getResolverSession()
                .getSession().getLocalRepository().getBasedir();

        File file1 = new File(repoDir,
                "commons-beanutils/commons-beanutils/1.9.4/commons-beanutils-1.9.4.jar");
        File file2 = new File(repoDir,
                "commons-logging/commons-logging/1.2/commons-logging-1.2.jar");
        File file3 = new File(repoDir,
                "commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar");

        assertThat(files, Matchers.contains(file1, file2, file3));
    }

    @Test
    public void testResolveWithDefaultsInOddjob() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(getClass()
                .getResource("/oddjob/Resolve/resolve-with-defaults.xml")
        .getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        ResolverSession resolverSession = lookup.lookup("resolve.resolverSession",
                ResolverSession.class);

        List<File> files = lookup.lookup("resolve.resolvedFiles",
                List.class);

        File repoDir = resolverSession.getSession().getLocalRepository().getBasedir();

        File file1 = new File(repoDir,
                "commons-beanutils/commons-beanutils/1.9.4/commons-beanutils-1.9.4.jar");
        File file2 = new File(repoDir,
                "commons-logging/commons-logging/1.2/commons-logging-1.2.jar");
        File file3 = new File(repoDir,
                "commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar");

        assertThat(files, contains(file1, file2, file3));
    }

}
