package aeterraes.handler.command;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ConfigCommand {
    void execute(List<Path> paths, ArrayNode result) throws IOException;
}
