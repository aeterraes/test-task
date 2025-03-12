package aeterraes.parser;

import static org.junit.jupiter.api.Assertions.*;

import aeterraes.exceptions.ConfigException;
import aeterraes.util.model.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class ConfigParserTest {
    private ConfigParser parser;
    private Path temp;

    @BeforeEach
    void setUp() throws IOException {
        parser = new ConfigParser();
        temp = Files.createTempFile("config_test", ".txt");
    }

    private void writeConfig(String content) throws IOException {
        Files.writeString(temp, content);
    }

    @Test
    void testCorrectlyParsed() throws IOException, ConfigException {
        String configContent = """
            #1
            mode: dir
            path: somefiles
            
            #2
            mode: dir
            path: somefiles
            
            #3
            mode: files
            path: somefiles/a.txt
            action: string
            
            #4
            mode: dir
            path: somefiles
            action: count
            
            #5
            mode: files
            path: somefiles/a.txt, somefiles/c.txt
            action: replace
            
            #6
            mode: dir
            path: somefiles
            action: replace
            """;
        writeConfig(configContent);

        List<Configuration> configurations = parser.parse(temp);

        assertEquals(6, configurations.size());

        Configuration config1 = configurations.getFirst();
        assertEquals(1, config1.id());
        assertEquals("dir", config1.mode());
        assertEquals(List.of("somefiles"), config1.path());
        assertEquals("string", config1.action());

        Configuration config2 = configurations.get(1);
        assertEquals(2, config2.id());
        assertEquals("dir", config2.mode());
        assertEquals(List.of("somefiles"), config2.path());
        assertEquals("string", config2.action());

        Configuration config3 = configurations.get(2);
        assertEquals(3, config3.id());
        assertEquals("files", config3.mode());
        assertEquals(List.of("somefiles/a.txt"), config3.path());
        assertEquals("string", config3.action());

        Configuration config4 = configurations.get(3);
        assertEquals(4, config4.id());
        assertEquals("dir", config4.mode());
        assertEquals(List.of("somefiles"), config4.path());
        assertEquals("count", config4.action());

        Configuration config5 = configurations.get(4);
        assertEquals(5, config5.id());
        assertEquals("files", config5.mode());
        assertEquals(List.of("somefiles/a.txt", "somefiles/c.txt"), config5.path());
        assertEquals("replace", config5.action());

        Configuration config6 = configurations.get(5);
        assertEquals(6, config6.id());
        assertEquals("dir", config6.mode());
        assertEquals(List.of("somefiles"), config6.path());
        assertEquals("replace", config6.action());
    }


    @Test
    void testEmptyFile() throws IOException {
        writeConfig("");
        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("Configuration file is empty", exception.getMessage());
    }

    @Test
    void testMissingID() throws IOException {
        String configContent = """
                mode: dir
                path: somefiles
                """;
        writeConfig(configContent);

        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("No config ID found before parameters", exception.getMessage());
    }

    @Test
    void testInvalidLineFormat() throws IOException {
        String configContent = """
                #1
                mode dir
                """;
        writeConfig(configContent);

        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("Invalid line in configuration: mode dir", exception.getMessage());
    }

    @Test
    void testInvalidMode() throws IOException {
        String configContent = """
                #1
                mode: invalid_mode
                path: somefiles
                """;
        writeConfig(configContent);

        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("Invalid mode in configuration: mode: invalid_mode", exception.getMessage());
    }

    @Test
    void testInvalidAction() throws IOException {
        String configContent = """
                #1
                mode: dir
                path: somefiles
                action: invalid_action
                """;
        writeConfig(configContent);

        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("Invalid action in configuration: action: invalid_action", exception.getMessage());
    }

    @Test
    void testEmptyValue() throws IOException {
        String configContent = """
                #1
                mode:
                """;
        writeConfig(configContent);

        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("Empty value in configuration: mode:", exception.getMessage());
    }

    @Test
    void testMissingRequiredParams() throws IOException {
        String configContent = """
                #1
                mode: dir
                """;
        writeConfig(configContent);

        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("Missing required parameters for ID: 1", exception.getMessage());
    }

    @Test
    void testTooFewIndices() throws IOException {
        String configContent = """
                #1
                mode: dir
                path: somefiles
                
                #2
                mode: dir
                path: somefiles
                
                #3
                mode: files
                path: somefiles/a.txt
                action: string
                """;
        writeConfig(configContent);

        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("Too few indices in configuration or duplicates was found", exception.getMessage());
    }

    @Test
    void testDuplicateIDs() throws IOException {
        String configContent = """
                #1
                mode: dir
                path: somefiles
                
                #1
                mode: dir
                path: somefiles
                """;
        writeConfig(configContent);

        Exception exception = assertThrows(ConfigException.class, () -> parser.parse(temp));
        assertEquals("Too few indices in configuration or duplicates was found", exception.getMessage());
    }
}
