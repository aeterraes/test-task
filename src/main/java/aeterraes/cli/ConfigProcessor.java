package aeterraes.cli;

import aeterraes.handler.ConfigHandler;
import aeterraes.parser.ConfigParser;
import aeterraes.util.model.Configuration;
import aeterraes.exceptions.ConfigException;
import aeterraes.util.ConfigurationStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigProcessor {
    public static void run(String[] args) throws IOException, ConfigException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Too few arguments");
        }

        String configPath = args[0];
        int configId;

        try {
            configId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Incorrect ConfigID");
            return;
        }

        Path configFilePath = Path.of(configPath);

        if (!Files.exists(configFilePath) || !Files.isRegularFile(configFilePath)) {
            System.out.println("File doesn't exist");
            return;
        }

        ConfigParser parser = new ConfigParser();

        List<Configuration> configurations = ConfigurationStorage.getConfigurations(configFilePath, parser);

        Configuration config = configurations.stream()
                .filter(c -> c.id() == configId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ConfigID " + configId + " not found"));

        ConfigHandler handler = new ConfigHandler(config, configPath);
        handler.processConfiguration();
    }
}

