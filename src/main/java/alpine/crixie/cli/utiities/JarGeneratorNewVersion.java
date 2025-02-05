package alpine.crixie.cli.utiities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JarGeneratorNewVersion {
    private static final String BUILD_DIR = "build";
    private static final String CLASSES_DIR = BUILD_DIR.concat(File.separator).concat("classes");
    private static final String MANIFEST_PATH = BUILD_DIR.concat(File.separator).concat("META-INF")
            .concat(File.separator).concat("MANIFEST.MF");

    private File jarFile;
    private PackageLuaModifier.PackageData packageMetaData;

    public JarGeneratorNewVersion(PackageLuaModifier packageLuaModifier) {
        packageMetaData = packageLuaModifier.getData();
    }

    public void generateJar() throws IOException, InterruptedException {
        String name = packageMetaData.name();
        String version = packageMetaData.version();
        String mainClass = packageMetaData.directoryWhereMainFileIs();
        String jarName = name + "@" + version + ".jar";

        // Define caminho final do JAR
        jarFile = new File(BUILD_DIR + File.separator + jarName);

        // Garante que os diretórios necessários existam
        new File(BUILD_DIR).mkdirs();
        new File(CLASSES_DIR).mkdirs();
        new File(BUILD_DIR.concat(File.separator).concat("META-INF")).mkdirs();

        // 🔹 1. Compilar os arquivos Java para build/classes
        compileJavaSources();

        // 🔹 2. Criar o arquivo MANIFEST.MF
        generateManifest(mainClass);

        // 🔹 3. Criar o JAR
        createJar();
    }

    private void compileJavaSources() throws IOException, InterruptedException {
        String[] compileCommand;

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Comando para Windows (usando PowerShell)
            compileCommand = new String[]{
                    "powershell.exe", "-Command",
                    "Get-ChildItem -Path src/main/java -Filter *.java -Recurse | ForEach-Object { $_.FullName } > sources.txt; " +
                            "javac --release 16 -d " + CLASSES_DIR + " -sourcepath src/main/java $(Get-Content sources.txt)"
            };
        } else {
            // Comando para Linux (usando shell)
            compileCommand = new String[]{
                    "/bin/bash", "-c",
                    "find src/main/java -name '*.java' > sources.txt; " +
                            "javac --release 16 -d " + CLASSES_DIR + " -sourcepath src/main/java @sources.txt"
            };
        }

        runCommand(compileCommand);
    }

    private void generateManifest(String mainClass) throws IOException {
        String manifestContent = "Manifest-Version: ".concat(packageMetaData.version()).concat("\n") +
                "Main-Class: " + mainClass + "\n";

        Files.write(Paths.get(MANIFEST_PATH), manifestContent.getBytes());
    }

    private void createJar() throws IOException, InterruptedException {
        // Comando para gerar o JAR com base nas classes compiladas
        String[] jarCommand = {
                "jar", "cfm", jarFile.getAbsolutePath(), MANIFEST_PATH, "-C", CLASSES_DIR, "."
        };

        runCommand(jarCommand);
    }

    private void runCommand(String[] command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Error on execute command: " + String.join(" ", command));
        }
    }

    public File getJarFile() {
        return jarFile;
    }
}