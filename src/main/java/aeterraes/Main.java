package aeterraes;

import aeterraes.cli.ConfigProcessor;
import aeterraes.exceptions.ConfigException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ConfigException {
        ConfigProcessor.run(args);
    }
}