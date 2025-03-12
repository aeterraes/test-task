package aeterraes.handler;

import aeterraes.handler.command.ConfigCommand;
import aeterraes.handler.command.CountCommand;
import aeterraes.handler.command.ReplaceCommand;
import aeterraes.handler.command.StringCommand;
import aeterraes.util.model.Configuration;
import aeterraes.util.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigHandler {
    private final Configuration config;
    private final String configPath;
    private final Map<String, ConfigCommand> commands = new HashMap<>();

    public ConfigHandler(Configuration config, String configFilePath) {
        this.config = config;
        this.configPath = configFilePath;
        commands.put("string", new StringCommand());
        commands.put("count", new CountCommand());
        commands.put("replace", new ReplaceCommand());
    }

    public void processConfiguration() throws IOException {
        System.out.println("Processing config ID: " + config.id());

        List<Path> paths = config.mode().equals("dir")
                ? FileUtils.getFilesFromDir(config.path().getFirst())
                : config.path().stream().map(Paths::get).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("configFile", Path.of(configPath).toString());
        node.put("configurationID", config.id());

        ObjectNode configData = mapper.createObjectNode();
        configData.put("mode", config.mode());
        configData.put("path", String.join(", ", config.path()));
        node.set("configurationData", configData);

        ObjectNode out = mapper.createObjectNode();
        ArrayNode result = mapper.createArrayNode();

        ConfigCommand command = commands.get(config.action());
        if (command == null) {
            throw new IllegalArgumentException("Unknown action: " + config.action());
        }

        command.execute(paths, result);

        out.set("result", result);
        node.set("out", out);

        Path outputPath = Paths.get("config_" + config.id() + ".json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), node);

        System.out.println("Saved to: " + outputPath.toAbsolutePath());
    }
}
