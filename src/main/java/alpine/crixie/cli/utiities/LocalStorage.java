package alpine.crixie.cli.utiities;

import alpine.crixie.cli.components.InitialDataComponent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalStorage {
    private Globals globals;
    private String token;
    private String name;
    private String userId;

    private final String FILE_NAME = "local_storage.lua";
    private final Path filePath;
    private final InitialDataComponent initialDataComponent = new InitialDataComponent();

    public LocalStorage() {
        String appData = System.getenv("APPDATA");
        Path directory = Paths.get(appData, "cryxie");
        this.filePath = directory.resolve(FILE_NAME);

        if (!Files.exists(filePath)) {
            createFileIfNotExists();
        } else {
            load();
        }
    }

    private void createFileIfNotExists() {
        String appData;

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            appData = System.getenv("APPDATA");
        } else {
            appData = System.getenv("XDG_CONFIG_HOME");
            if (appData == null || appData.isBlank()) {
                appData = System.getProperty("user.home") + "/.config";
            }
        }

        Path directory = Paths.get(appData, "cryxie");
        Path filePath = directory.resolve(FILE_NAME);

        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            if (!Files.exists(filePath)) {
                try (var writer = new FileWriter(filePath.toFile())) {
                    writer.write(initialDataComponent.localStorageComponent());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load() throws RuntimeException {
        try {
            globals = JsePlatform.standardGlobals();

            LuaValue chunk = globals.load(new FileInputStream(filePath.toFile()),
                    FILE_NAME, "t", globals);
            chunk.call();

            name = getLuaString("Name");
            token = getLuaString("Token");
            userId = getLuaString("UserId");

        } catch (Exception e) {
            createFileIfNotExists();
            updateData(new Data("", "", ""));
        }
    }

    private String getLuaString(String variable) {
        LuaString luaString = (LuaString) globals.get(variable);
        if (luaString.equals(LuaValue.NIL)) {
            luaString = LuaString.valueOf("");
            globals.set(variable, luaString);
        }
        return luaString.tojstring();
    }

    public void updateData(Data data) {
        if (data.token == null) {
            throw new IllegalArgumentException("Token cannot be null.");
        }

        this.token = data.token;
        this.name = data.name;
        this.userId = data.userId;

        globals.set("Token", LuaString.valueOf(token));

        saveChangesAtLuaFile();
    }

    private void saveChangesAtLuaFile() {
        if (!Files.exists(filePath)) {
            createFileIfNotExists();
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8))) {
            writer.write(content());
        } catch (IOException e) {
            throw new RuntimeException("Error saving data to local storage: " + e.getMessage());
        }
    }

    private String content() {
        return "Token = \"" + token + "\"\n" +
                "Name = \"" + name + "\"\n" +
                "UserId = \"" + userId + "\"\n";
    }

    public Data getData() {
        if (userId == null) {
            return new Data();
        }
        // return new Data(token, decoder.decode(profilePicture), decoder.decode(name),
        // decoder.decode(userId));
        return new Data(token, name, userId);
    }

    public record Data(String token, String name, String userId) {
        public Data() {
            this("", "", "");
        }
    }
}
