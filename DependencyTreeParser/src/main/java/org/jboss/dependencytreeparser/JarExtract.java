package org.jboss.dependencytreeparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarExtract {

    public static void jarExtract(Path archiveFile, Path destPath) throws IOException {
        Files.createDirectories(destPath); // create dest path folder(s)

        try (ZipFile archive = new ZipFile(archiveFile.toFile())) {

            List<? extends ZipEntry> entries = archive.stream()
                    .sorted(Comparator.comparing(ZipEntry::getName))
                    .collect(Collectors.toList());

            for (ZipEntry entry : entries) {
                Path entryDest = destPath.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectory(entryDest);
                    continue;
                }

                Files.copy(archive.getInputStream(entry), entryDest);
            }
        }
    }
}
