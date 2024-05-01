package org.oddjob.maven.types;

import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.io.File;

/**
 * @oddjob.description Provide a list of dependencies. This may be nested.
 *
 * @oddjob.example Resolve a list of dependencies.
 *
 * {@oddjob.xml.resource oddjob/Resolve/resolve-many-with-defaults.xml}
 */
public class DependenciesFactory implements ValueFactory<DependencyContainer> {

    /**
     * @oddjob.description List of dependencies.
     * @oddjob.required No.
     */
    private final ListSetterHelper<DependencyContainer> dependencies = ListSetterHelper.newInstance();

    /**
     * @oddjob.description A text file of dependencies.
     * @oddjob.required No.
     */
    private File file;

    @Override
    public DependencyContainer toValue() {

        Dependencies dependencies = new Dependencies();
        dependencies.setFile(file);

        for (DependencyContainer dependency: this.dependencies.getList()) {
            if (dependency instanceof Dependency) {
                dependencies.addDependency((Dependency) dependency);
            }
            else if (dependency instanceof Dependencies) {
                dependencies.addDependencies((Dependencies) dependency);
            }
            else {
                throw new IllegalArgumentException("Unknown Dependency Container " + dependency);
            }
        }

        return dependencies;
    }

    public void setDependencies(int index, DependencyContainer dependency) {
        dependencies.set(index, dependency);
    }

    public void getDependencies(int index) {
        dependencies.get(index);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
