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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Dependencies
        implements DependencyContainer {

    private File file;

    private final List<DependencyContainer> containers = new ArrayList<>();

    private final List<Exclusion> exclusions = new ArrayList<>();

    private boolean nestedDependencies;

    public void validate() {

        Map<String, String> ids = new HashMap<>();
        for (DependencyContainer container : containers) {
            container.validate();
            if (container instanceof Dependency) {
                Dependency dependency = (Dependency) container;
                String id = dependency.getVersionlessKey();
                String collision = ids.put(id, dependency.getVersion());
                if (collision != null) {
                    throw new ResolveException("You must not declare multiple <dependency> elements"
                            + " with the same coordinates but got " + id + " -> " + collision + " vs "
                            + dependency.getVersion());
                }
            }
        }
    }

    public void setFile(File file) {
        this.file = file;
        checkExternalSources();
    }

    public File getFile() {
        return file;
    }



    private void checkExternalSources() {
        if ((file != null) && nestedDependencies) {
            throw new ResolveException("You must not specify both a file/POM and nested dependency collections");
        }
    }

    public void addDependency(Dependency dependency) {
        containers.add(dependency);
    }

    public void addDependencies(Dependencies dependencies) {
        containers.add(dependencies);
        nestedDependencies = true;
        checkExternalSources();
    }

    public List<DependencyContainer> getDependencyContainers() {
        return containers;
    }

    public void addExclusion(Exclusion exclusion) {
        this.exclusions.add(exclusion);
    }

    public List<Exclusion> getExclusions() {
        return exclusions;
    }

}
