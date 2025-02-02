package alpine.crixie.cli.components;

import br.com.cryxie.GenerateMainClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PackageLuaComponent {

    public PackageLuaComponent() {
        _make("", "", "0.0.1", "", "");
    }

    public PackageLuaComponent(
            String name, String packageName, String version, String repoUrl, String description) {
        _make(name, packageName, version, repoUrl, description);
    }

    private static void generateLuaFile(String name, String packageName, String version, String repoUrl, String description) {
        String currentDir = System.getProperty("user.dir");
        File newFile = new File(currentDir, "package.lua");

        String content = """
                ---
                --- Config related to your package
                ---
                
                Name = "%s"
                DirectoryWhereMainFileIs = "%s"
                Description = "%s"
                Version = "%s"
                
                RepositoryUrl = "%s"
                
                Dependencies = {}
                
                Visibility = "public"
                """.formatted(name, packageName, description, version, repoUrl);

        try (var writer = new FileWriter(newFile)) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void _make(String name, String packageName,
                       String version, String repoUrl,
                       String description) {

        generateLuaFile(name, packageName, version, repoUrl, description);
        generateJavaProject();
    }

    private void generateJavaProject() {
        new GenerateMainClass().generate((directory) -> {
        });
    }
}
