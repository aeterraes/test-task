package aeterraes.handler.command;

import aeterraes.util.FileUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CountCommand implements ConfigCommand{

    @Override
    public void execute(List<Path> paths, ArrayNode result) throws IOException {
        List<List<String>> allLines = FileUtils.readFiles(paths);
        for (int i = 0; ; i++) {
            boolean hasData = false;
            ArrayNode count = result.addArray();
            for (List<String> lines : allLines) {
                count.add(i < lines.size() ? lines.get(i).split("\\s+").length : 0);
                hasData = hasData || i < lines.size();
            }
            if (!hasData) break;
        }
    }
}
