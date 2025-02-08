package alpine.crixie.cli.utiities;

import alpine.crixie.cli.components.PackageLuaComponent;
import alpine.crixie.cli.utiities.requests.dtos.PackageRequestDto;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PackageLuaModifier {
    private Globals globals;
    private LuaTable dependenciesTable;
    private String name;
    private String description;
    private String version;
    private String repositoryUrl;
    private String directoryWhereMainFileIs;
    private List<PackageRequestDto.Dependency> deps = new ArrayList<>();
    private static final String LUA_FILE_PATH = "package.lua";
    private String visibility;

    public PackageLuaModifier() throws FileNotFoundException {
        File luaFile = new File(LUA_FILE_PATH);
        //System.out.println("Absolute path: " + luaFile.getAbsolutePath());
        if (!luaFile.exists()) {
            new PackageLuaComponent();
        }
        load();
    }

    public void load() {
        try {
            globals = JsePlatform.standardGlobals();

            LuaValue chunk = globals.load(new FileInputStream(LUA_FILE_PATH),
                    LUA_FILE_PATH, "t", globals);

            chunk.call();

            LuaTable dependenciesTableT = (LuaTable) globals.get("Dependencies");
            if (dependenciesTableT.equals(LuaValue.NIL)) {
                dependenciesTableT = new LuaTable();  // Cria uma nova tabela se não existir
                globals.set("Dependencies", dependenciesTableT);
            }
            dependenciesTable = dependenciesTableT;

            for (LuaValue dep_key : dependenciesTable.keys()) {
                var key = dep_key.tojstring();
                var value = dependenciesTable.get(key).tojstring();

                deps.add(new PackageRequestDto.Dependency(key, value));
            }

            directoryWhereMainFileIs = getLuaString("DirectoryWhereMainFileIs");
            name = getLuaString("Name");
            description = getLuaString("Description");
            version = getLuaString("Version");
            repositoryUrl = getLuaString("RepositoryUrl");
            visibility = getLuaString("Visibility");

        } catch (Exception e) {
            throw new RuntimeException("Error loading Lua file", e);
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
        }
    }

    private void saveLuaFile() {
        // Recria o arquivo Lua com as alterações
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(getLuaFilePath()), "UTF-8"))) {
            writer.write(getLuaFileContent());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o arquivo Lua", e);
        }
    }

    private String getLuaFilePath() {
        return new File(LUA_FILE_PATH).getPath();
    }

    public String getLuaFileContent() {
        // Recupera o conteúdo atualizado do arquivo Lua
        String dependenciesContent = Arrays.stream(dependenciesTable.keys()).
                map(key -> String.format("  [\"%s\"] = \"%s\",", key.tojstring(), dependenciesTable.get(key).tojstring()))
                .collect(Collectors.joining("\n"));

        return """
                -- Config related to your package
                
                Name = "%s"
                DirectoryWhereMainFileIs = "%s"
                Description = "%s"
                Version = "%s"
                RepositoryUrl = "%s"
                
                Dependencies = {
                %s
                }
                
                Visibility = "%s"
                """.formatted(
                name, directoryWhereMainFileIs, description, version, repositoryUrl, dependenciesContent, visibility
        );
    }

    public PackageData getData() {
        return new PackageData(this.name, this.version, this.directoryWhereMainFileIs,
                this.description,
                this.repositoryUrl, deps,
                PackageRequestDto.Visibility.valueOf(visibility.toUpperCase()));
    }

    public record PackageData(
            String name, String version, String directoryWhereMainFileIs,
            String description, String repositoryUrl,
            List<PackageRequestDto.Dependency> deps,
            PackageRequestDto.Visibility visibility) {
    }

    public String generateNameForBuiltPackage() {
        String name = getData().name();
        String version = getData().version();
        return name + "@" + version + ".jar";
    }
}

