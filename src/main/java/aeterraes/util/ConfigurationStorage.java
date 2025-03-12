package aeterraes.util;

import aeterraes.exceptions.ConfigException;
import aeterraes.parser.ConfigParser;
import aeterraes.util.model.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfigurationStorage {
    private static final Map<String, List<Configuration>> cache = new HashMap<>();

    private ConfigurationStorage() {}

    public static List<Configuration> getConfigurations(Path path, ConfigParser parser) throws ConfigException, IOException {
        String key = path.toAbsolutePath().toString();
        System.out.println(key);
        if (cache.containsKey(key)) {
            System.out.println("Using cached configuration for: " + key);
            return cache.get(key);
        }
        List<Configuration> configurations = parser.parse(path);
        cache.put(key, configurations);
        return configurations;
    }
}
