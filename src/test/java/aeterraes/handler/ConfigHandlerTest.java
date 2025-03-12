package aeterraes.handler;

import aeterraes.util.model.Configuration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigHandlerTest {
    private static final String config = "data.csv";
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Configuration> configurations;

    @BeforeEach
    void setUp() {
        configurations = List.of(
                new Configuration(3, "files", List.of("somefiles/a.txt"), "string"),
                new Configuration(4, "files", List.of("somefiles/a.txt", "somefiles/b.txt", "somefiles/c.txt"), "count"),
                new Configuration(5, "files", List.of("somefiles/a.txt", "somefiles/c.txt"), "replace")
        );
    }

    @Test
    void testProcessStringConfiguration() throws IOException {
        ConfigHandler handler = new ConfigHandler(configurations.getFirst(), config);
        handler.processConfiguration();
        JsonNode actual = mapper.readTree(new File("config_3.json"));
        JsonNode expected = mapper.readTree("""
            {
              "configFile" : "data.csv",
              "configurationID" : 3,
              "configurationData" : {
                "mode" : "files",
                "path" : "somefiles/a.txt"
              },
              "out" : {
                "result" : [ [ "In in pellentesque sem" ], [ "In quis urna" ], [ "ut" ], [ "enim convallis" ], [ "" ] ]
              }
            }
        """);
        assertEquals(expected, actual);
    }

    @Test
    void testProcessCountConfiguration() throws IOException {
        ConfigHandler handler = new ConfigHandler(configurations.get(1), config);
        handler.processConfiguration();
        JsonNode actual = mapper.readTree(new File("config_4.json"));
        JsonNode expected = mapper.readTree("""
            {
              "configFile" : "data.csv",
              "configurationID" : 4,
              "configurationData" : {
                "mode" : "files",
                "path" : "somefiles/a.txt, somefiles/b.txt, somefiles/c.txt"
              },
              "out" : {
                "result" : [ [ 4, 1, 2 ], [ 3, 1, 2 ], [ 1, 2, 5 ], [ 2, 2, 1 ], [ 0, 4, 0 ], [ 0, 0, 0 ] ]
              }
            }
        """);
        assertEquals(expected, actual);
    }

    @Test
    void testProcessReplaceConfiguration() throws IOException {
        ConfigHandler handler = new ConfigHandler(configurations.get(2), config);
        handler.processConfiguration();
        JsonNode actual = mapper.readTree(new File("config_5.json"));
        JsonNode expected = mapper.readTree("""
            {
              "configFile" : "data.csv",
              "configurationID" : 5,
              "configurationData" : {
                "mode" : "files",
                "path" : "somefiles/a.txt, somefiles/c.txt"
              },
              "out" : {
                "result" : [ [ "914 914 16512125142051917215 19513", "141619310 14161362021106" ],
                             [ "914 1721919 2118141", "52171032220 516131619," ],
                             [ "2120", "621 2213132144161917619 20614 14161362021106 2221" ],
                             [ "514913 315142211212919", "5161564." ],
                             [ "", "" ] ]
              }
            }
        """);
        assertEquals(expected, actual);
    }
}
