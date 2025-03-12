package aeterraes.handler.command;

import aeterraes.util.FileUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class StringCommand implements ConfigCommand {
    @Override
    public void execute(List<Path> paths, ArrayNode result) throws IOException {
        List<List<String>> allLines = FileUtils.readFiles(paths);
        for (int i = 0; ; i++) {
            boolean hasData = false;
            ArrayNode line = result.addArray();
            for (List<String> lines : allLines) {
                if (i < lines.size()) {
                    line.add(lines.get(i));
                    hasData = true;
                } else {
                    line.add("");
                }
            }
            if (!hasData) break;
        }
    }
}

