package alpine.crixie.cli.utiities;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.util.Properties;

public class PackageLuaModifier {
    private static PackageLuaModifier instance = null;

    private final Globals globals;
    private final LuaTable dependenciesTable;
    private final String name;
    private final String description;
    private final String version;
    private final String repositoryUrl;

    public static PackageLuaModifier getInstance() throws FileNotFoundException {
        final String luaFilePath = "package.lua";

        if (instance == null) {
            File luaFile = new File(luaFilePath);
            if (!luaFile.exists()) {
                throw new RuntimeException("file package.lua not found at " + luaFilePath);
            }
            instance = new PackageLuaModifier(new FileInputStream(luaFile));
        }
        return instance;
    }

    public static PackageLuaModifier createInstance(InputStream luaFileStream) {
        if (luaFileStream == null) {
            throw new RuntimeException("O InputStream fornecido é nulo.");
        }
        return new PackageLuaModifier(luaFileStream);
    }

    private PackageLuaModifier(InputStream luaFileStream) throws RuntimeException {
        try {
            globals = JsePlatform.standardGlobals();

            LuaValue chunk = globals.load(luaFileStream, "package.lua", "t", globals);
            chunk.call();

            LuaTable mainTable = (LuaTable) globals.get("Dependencies");
            if (mainTable.equals(LuaValue.NIL)) {
                mainTable = new LuaTable();  // Cria uma nova tabela se não existir
                globals.set("Dependencies", mainTable);
            }
            dependenciesTable = mainTable;

            LuaString nameT = (LuaString) globals.get("Name");
            if (nameT.equals(LuaValue.NIL)) {
                nameT = LuaString.valueOf("");
                globals.set("Name", nameT);
            }
            name = nameT.tojstring();

            LuaString descT = (LuaString) globals.get("Description");
            if (descT.equals(LuaValue.NIL)) {
                descT = LuaString.valueOf("");
                globals.set("Description", descT);
            }
            description = descT.tojstring();

            LuaString versionT = (LuaString) globals.get("Version");
            if (versionT.equals(LuaValue.NIL)) {
                versionT = LuaString.valueOf("");
                globals.set("Version", versionT);
            }
            version = versionT.tojstring();

            LuaString repoT = (LuaString) globals.get("RepositoryUrl");
            if (repoT.equals(LuaValue.NIL)) {
                repoT = LuaString.valueOf("");
                globals.set("RepositoryUrl", repoT);
            }
            repositoryUrl = repoT.tojstring();


        } catch (Exception e) {
            throw new RuntimeException("Error loading Lua file", e);
        }
    }

    public void addDependency(String name, String version) {
        if (version == null) version = "@latest";

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        // Adiciona o nome e versão à tabela Dependencies
        dependenciesTable.set(LuaValue.valueOf(name), LuaValue.valueOf(version));

        // Salva as mudanças no arquivo Lua
        saveLuaFile();
    }

    public void removeDependency(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Dependency name cannot be null or empty.");
        }

        // Remove a dependência da tabela Dependencies
        LuaValue dependencyKey = LuaValue.valueOf(name);
        if (!dependenciesTable.get(dependencyKey).isnil()) {
            dependenciesTable.set(dependencyKey, LuaValue.NIL);

            // Salva as mudanças no arquivo Lua
            saveLuaFile();
        } else {
            System.out.println("Dependency '" + name + "' not found.");
        }
    }

    private void saveLuaFile() {
        // Recria o arquivo Lua com as alterações
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(getLuaFilePath()), "UTF-8"))) {
            writer.write(getLuaFileContent());
        } catch (IOException e) {
            throw new RuntimeException("Error saving package.lua file", e);
        }
    }

    private String getLuaFilePath() {
        return new File("package.lua").getPath();
    }

    private String getLuaFileContent() {
        // Recupera o conteúdo atualizado do arquivo Lua
        StringBuilder content = new StringBuilder();

        content.append("-- Config related to your package\n\n");

        content.append("Name = \"").append(name).append("\"\n");
        content.append("Description = \"").append(description).append("\"\n");
        content.append("Version = \"").append(version).append("\"\n");
        content.append("RepositoryUrl = \"").append(repositoryUrl).append("\"\n");
        content.append("\n");
        content.append("Dependencies = {\n");
        for (LuaValue key : dependenciesTable.keys()) {
            content.append("  [\"").append(key.tojstring()).append("\"] = \"")
                    .append(dependenciesTable.get(key).tojstring()).append("\",\n");
        }
        content.append("}\n");

        return content.toString();
    }

    public Properties toProperties(String mainTableName) throws IllegalArgumentException {
        // Tabela principal no arquivo Lua
        LuaTable mainTable = (LuaTable) globals.get(mainTableName);

        if (mainTable.equals(LuaValue.NIL)) {
            throw new IllegalArgumentException("Dependencies = {} not founded" + mainTableName);
        }

        Properties props = new Properties();
        for (LuaValue key : mainTable.keys()) {
            props.put(key.tojstring(), mainTable.get(key).tojstring());
        }
        return props;
    }

}

