package alpine.crixie.cli.components;

import br.com.cryxie.GenerateMainClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PackageLuaComponent {
    public PackageLuaComponent() {
        _make("", "0.0.1", "", "", "N");
    }

    public PackageLuaComponent(
            String packageName, String version, String repoUrl, String description, String generateProject) {
        _make(packageName, version, repoUrl, description, generateProject);
    }

    public PackageLuaComponent(
            String packageName, String version, String repoUrl, String description) {
        _make(packageName, version, repoUrl, description, "N");
    }

    private void _make(String packageName,
                       String version, String repoUrl,
                       String description, String generateProject) {

        generateLuaFile(packageName, version, repoUrl, description);
        generateJavaProject(generateProject);
    }

    private void generateJavaProject(String generateProject) {
        if (canGenerateJavaProject(generateProject)) {
            new GenerateMainClass().generate((directory) -> {
            });
        }
    }

    private boolean canGenerateJavaProject(String generateProject) {
        return !generateProject.isEmpty() && (generateProject.trim().equalsIgnoreCase("y") ||
                generateProject.trim().equalsIgnoreCase("yes"));
    }

    private static void generateLuaFile(String packageName, String version, String repoUrl, String description) {
        String currentDir = System.getProperty("user.dir");
        File newFile = new File(currentDir, "package.lua");


        String content = """
                ---
                --- Config related to your package
                ---
                
                Name = "%s"
                DirectoryWhereMainFileIs = ""
                Description = "%s"
                Version = "%s"
                
                RepositoryUrl = "%s"
                
                Dependencies = {}
                
                Visibility = "public"
                """.formatted(packageName, description, version, repoUrl);

        try (var writer = new FileWriter(newFile)) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
