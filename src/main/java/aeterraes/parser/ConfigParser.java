package aeterraes.parser;

import aeterraes.exceptions.ConfigException;
import aeterraes.util.model.Configuration;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConfigParser {
    private static final Set<String> modes = Set.of("dir", "files");
    private static final Set<String> actions = Set.of("count", "replace", "string");
    private static final String defaultAction = "string";

    public List<Configuration> parse(Path path) throws IOException, ConfigException {
        List<String> contents = Files.readAllLines(path);
        if (contents.isEmpty()) {
            throw new ConfigException("Configuration file is empty");
        }
        List<Configuration> configs = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        Integer currentId = null;
        String mode = null;
        String action = defaultAction;
        Set<Integer> indices = new HashSet<>();

        for (String line : contents) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                if (line.matches("#\\d+")) {
                    if (currentId != null) {
                        if (mode == null || paths.isEmpty()) {
                            throw new ConfigException("Missing required parameters for ID: " + currentId);
                        }
                        configs.add(new Configuration(currentId, mode, new ArrayList<>(paths), action));
                    }

                    currentId = Integer.parseInt(line.substring(1));
                    indices.add(currentId);
                    mode = null;
                    action = defaultAction;
                    paths.clear();
                }
                continue;
            }

            if (currentId == null) {
                throw new ConfigException("No config ID found before parameters");
            }

            String[] parts = line.split(":", 2);
            if (parts.length != 2) {
                throw new ConfigException("Invalid line in configuration: " + line);
            }

            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();

            if (value.isEmpty()) {
                throw new ConfigException("Empty value in configuration: " + line);
            }

            switch (key) {
                case "mode":
                    if (!modes.contains(value)) {
                        throw new ConfigException("Invalid mode in configuration: " + line);
                    }
                    mode = value;
                    break;
                case "action":
                    if (!actions.contains(value)) {
                        throw new ConfigException("Invalid action in configuration: " + line);
                    }
                    action = value;
                    break;
                case "path":
                    paths.addAll(Arrays.stream(value.split(","))
                            .map(String::trim)
                            .toList());
                    break;
                default:
                    throw new ConfigException("Unknown configuration parameter: " + key);
            }
        }

        if (currentId != null) {
            if (mode == null || paths.isEmpty()) {
                throw new ConfigException("Missing required parameters for ID: " + currentId);
            }
            configs.add(new Configuration(currentId, mode, new ArrayList<>(paths), action));
        }

        if (indices.size() < 5) {
            throw new ConfigException("Too few indices in configuration or duplicates was found");
        }

        validateConfigurations(configs);

        return configs;
    }

    private void validateConfigurations(List<Configuration> configs) throws ConfigException {
        for (Configuration config : configs) {
            for (String path : config.path()) {
                Path p = Paths.get(path);
                if (config.mode().equals("dir")) {
                    if (config.path().size() > 1) {
                        throw new ConfigException("mode=dir but more than one argument was found in " + config.id());
                    }
                    if (!Files.isDirectory(p) || isDirectoryEmpty(p)) {
                        throw new ConfigException("Directory " + path + " is empty or doesn't exist");
                    }
                } else if (config.mode().equals("files")) {
                    if (!Files.exists(p) || Files.isDirectory(p)) {
                        throw new ConfigException("File " + path + " doesn't exist or it's a directory");
                    }
                }
            }
        }
    }

    private boolean isDirectoryEmpty(Path dir) throws ConfigException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
            return !dirStream.iterator().hasNext();
        } catch (IOException e) {
            throw new ConfigException("Something went wrong: " + dir);
        }
    }
}