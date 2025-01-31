package alpine.crixie.cli.utiities;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LocalStorage {
    private Globals globals;
    private String token;
    private String name;
    private String profilePicture;
    private String userId;

    private final String FILE_NAME = "local_storage.lua";

    public LocalStorage() {
        File luaFile = new File(FILE_NAME);
        if (!luaFile.exists()) {
            String currentDir = System.getProperty("user.dir");
            File newFile = new File(currentDir, FILE_NAME);

            try (var writer = new FileWriter(newFile)) {
                writer.write(
                        """
                                Token = ""
                                Name = ""
                                ProfilePicture = ""
                                UserId = ""
                                """
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            load();
        }
    }

    private void load() throws RuntimeException {
        try {
            globals = JsePlatform.standardGlobals();

            LuaValue chunk = globals.load(new FileInputStream(FILE_NAME),
                    FILE_NAME, "t", globals);
            chunk.call();

            name = getLuaString("Name");
            profilePicture = getLuaString("ProfilePicture");
            token = getLuaString("Token");
            userId = getLuaString("UserId");

        } catch (Exception e) {
            throw new RuntimeException("Error loading localStorage", e);
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
        this.profilePicture = data.profilePicture;
        this.userId = data.userId;

        globals.set("Token", LuaString.valueOf(token));

        saveChangesAtLuaFile();
    }

    private void saveChangesAtLuaFile() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(getLuaFilePath()), StandardCharsets.UTF_8))) {
            writer.write(content());
        } catch (IOException e) {
            throw new RuntimeException("Error on save data at local storage", e);
        }
    }

    private String getLuaFilePath() {
        return new File(FILE_NAME).getPath();
    }

    private String content() {
        return "Token = \"" + token + "\"\n" +
                "Name = \"" + name + "\"\n" +
                "ProfilePicture = \"" + profilePicture + "\"\n" +
                "UserId = \"" + userId + "\"\n";
    }

    public record Data(String token, String profilePicture, String name, String userId) {
    }

    public Data getData() {
        var decoder = new Base64Decoder();
        return new Data(token, decoder.decode(profilePicture), decoder.decode(name), decoder.decode(userId));
    }
}

