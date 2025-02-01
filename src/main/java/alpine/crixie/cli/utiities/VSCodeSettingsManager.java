package alpine.crixie.cli.utiities;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VSCodeSettingsManager {
    private static final String VSCODE_DIR = ".vscode";
    private static final String SETTINGS_FILE = VSCODE_DIR.concat(File.separator).concat("/settings.json");
    private static final String KEY_REFERENCED_LIBRARIES = "java.project.referencedLibraries";
    private static final String DEFAULT_DEPENDENCY = "cryxie_libs/**/*.jar";

    public VSCodeSettingsManager() {
        ensureSettingsFileExists();
    }

    private void ensureSettingsFileExists() {
        try {
            Path vscodePath = Paths.get(VSCODE_DIR);
            Path settingsPath = Paths.get(SETTINGS_FILE);

            if (!Files.exists(vscodePath)) {
                Files.createDirectories(vscodePath);
            }

            if (!Files.exists(settingsPath)) {
                JsonObject json = new JsonObject();
                JsonArray dependencies = new JsonArray();
                dependencies.add(DEFAULT_DEPENDENCY);
                json.add(KEY_REFERENCED_LIBRARIES, dependencies);

                writeSettings(json);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao garantir a existência do settings.json", e);
        }
    }

    private void writeSettings(JsonObject json) {
        try (Writer writer = new FileWriter(SETTINGS_FILE)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao escrever no settings.json", e);
        }
    }
}
