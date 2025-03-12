package aeterraes.handler.command;

import aeterraes.util.FileUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ReplaceCommand implements ConfigCommand {
    @Override
    public void execute(List<Path> paths, ArrayNode result) throws IOException {
        List<List<String>> allLines = FileUtils.readFiles(paths);
        for (int i = 0; ; i++) {
            boolean hasData = false;
            ArrayNode line = result.addArray();
            for (int fileInd = 0; fileInd < allLines.size(); fileInd++) {
                List<String> lines = allLines.get(fileInd);
                if (i < lines.size()) {
                    StringBuilder sb = new StringBuilder();
                    for (char ch : lines.get(i).toCharArray()) {
                        if (Character.isLowerCase(ch)) {
                            int toNumber = (ch - 'a' + 1) + fileInd;
                            sb.append(toNumber);
                        } else if (Character.isUpperCase(ch)) {
                            int toNumber = (ch - 'A' + 1) + fileInd;
                            sb.append(toNumber);
                        } else {
                            sb.append(ch);
                        }
                    }
                    line.add(sb.toString());
                    hasData = true;
                } else {
                    line.add("");
                }
            }
            if (!hasData) break;
        }
    }
}
