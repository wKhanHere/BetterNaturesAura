package net.wkhan.naturesaura_plus.data.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManualConfig {
    private static final Path CONFIG_PATH = Paths.get("config","naturesaura_plus" ,"logging_config.toml");
    private static final Set<String> ALLOWED_MOD_IDS = new HashSet<>();
    private static boolean enabled = true;

    public static void load() {
        ALLOWED_MOD_IDS.clear();
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (!Files.exists(CONFIG_PATH)) {
                List<String> defaultLines = List.of(
                        "# NaturesAuraPlus Config File",
                        "# Set to false to allow error messages regarding missing mod ids for NaturesAuraPlus JSONs.",
                        "pleaseUnfloodMyLogs = true",
                        "",
                        "# Values inside the array below are treated as mod ids for whom error logs will be shown even if they are not installed.",
                        "# Values should be separated by commas and surrounded in quotes. (All values should be contained in a single line)",
                        "allowed_mod_ids = []"
                );
                Files.write(CONFIG_PATH, defaultLines);
            }

            List<String> lines = Files.readAllLines(CONFIG_PATH);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                if (line.startsWith("pleaseUnfloodMyLogs")) {
                    String value = extractTomlValue(line);
                    enabled = Boolean.parseBoolean(value);
                } else if (line.startsWith("allowed_mod_ids")) {
                    parseTomlArray(line);
                }
            }
        } catch (IOException e) {
            System.err.println("[NaturesAuraPlus] Failed to load manual config file: " + e.getMessage());
        }
    }

    private static String extractTomlValue(String line) {
        int eqIndex = line.indexOf('=');
        if (eqIndex != -1)
            return line.substring(eqIndex + 1).trim();
        return "";
    }

    private static void parseTomlArray(String line) {
        int openBracket = line.indexOf('[');
        int closeBracket = line.indexOf(']');

        if (openBracket == -1 || closeBracket == -1 || closeBracket <= openBracket)
            return;

        String arrayContent = line.substring(openBracket + 1, closeBracket);
        String[] elements = arrayContent.split(",");

        for (String element : elements) {
            String mod_id = element.trim().replace("\"", "").replace("'", "").toLowerCase();
            if (!mod_id.isEmpty())
                ALLOWED_MOD_IDS.add(mod_id);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static Set<String> getAllowedModIds() {
        return Collections.unmodifiableSet(ALLOWED_MOD_IDS);
    }
}
