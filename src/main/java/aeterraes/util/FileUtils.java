package aeterraes.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private FileUtils() {}

    public static List<Path> getFilesFromDir(String dirPath) throws IOException {
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dirPath))) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    paths.add(path);
                }
            }
        }
        return paths;
    }

    public static List<List<String>> readFiles(List<Path> paths) throws IOException {
        List<List<String>> allLines = new ArrayList<>();
        for (Path path : paths) {
            allLines.add(Files.readAllLines(path));
        }
        return allLines;
    }
}

