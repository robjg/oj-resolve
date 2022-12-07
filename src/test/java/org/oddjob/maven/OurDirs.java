package org.oddjob.maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Used to work out relative directories, when running tests individually
 * from an IDE or from Maven.
 * <p/>
 *
 * @author rob
 */
public class OurDirs {
    private static final Logger logger = LoggerFactory.getLogger(OurDirs.class);

    private static final OurDirs INSTANCE = new OurDirs();

    private final Path buildDirPath;

    private final Path workDirPath;

    public OurDirs() {

        String baseDir = System.getProperty("basedir");

        if (baseDir == null) {
            baseDir = ".";
        }

        buildDirPath = Paths.get(baseDir, "target");
        try {
            workDirPath = mkDirs(buildDirPath.resolve("work"), false);
        } catch (IOException e) {
            throw new IllegalStateException("Failed creating work dir.", e);
        }

        logger.info("Work Dir path is {}", workDirPath);

    }

    public static Path buildDirPath() {
        return INSTANCE.buildDirPath;
    }

    public static Path workDirPath() throws IOException {

        return INSTANCE.workDirPath;
    }

    public static Path workPathDir(String dirName, boolean recreate) throws IOException {
        return mkDirs(workDirPath().resolve(dirName), recreate);
    }

    private static Path mkDirs(Path dir, boolean recreate) throws IOException {
        if (Files.exists(dir)) {
            if (recreate) {
                deleteDir(dir);
            } else {
                if (!Files.isDirectory(dir)) {
                    throw new IllegalArgumentException(dir + " is not a directory");
                }
                return dir;
            }
        }
        return Files.createDirectories(dir);
    }

    private static void deleteDir(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
