package aeterraes.util.model;

import java.util.List;

public record Configuration(int id, String mode, List<String> path, String action) {}