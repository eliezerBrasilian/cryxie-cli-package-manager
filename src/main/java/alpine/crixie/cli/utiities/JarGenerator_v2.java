package alpine.crixie.cli.utiities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

public class JarGenerator_v2 {
    private static final String BUILD_DIR = "build";
    private static final String CLASSES_DIR = BUILD_DIR + File.separator + "classes";
    private static final String MANIFEST_PATH = BUILD_DIR + File.separator + "META-INF"
            + File.separator + "MANIFEST.MF";
    public static final String LIB_PATH = "cryxie_libs";
    public static final String SRC_PATH = "src" + File.separator + "main" + File.separator + "java";

    private File jarFile;
    private final PackageLuaModifier.PackageData packageMetaData;

    public JarGenerator_v2(PackageLuaModifier packageLuaModifier) {
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
        new File(BUILD_DIR + File.separator + "META-INF").mkdirs();

        // 1. Compilar os arquivos Java para build/classes
        compileJavaSources();

        // 2. Copiar arquivos adicionais (não .java) de SRC_PATH para CLASSES_DIR
        copyAdditionalFiles();

        // 3. Criar o arquivo MANIFEST.MF
        generateManifest(mainClass);

        // 4. Criar o JAR
        createJar();
    }

    private void compileJavaSources() throws IOException, InterruptedException {
        String[] compileCommand;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            compileCommand = new String[] {
                    "powershell.exe", "-Command",
                    "Get-ChildItem -Path " + SRC_PATH
                            + " -Filter *.java -Recurse | ForEach-Object { $_.FullName } > sources.txt; " +
                            "javac --release 16 -d " + CLASSES_DIR + " -sourcepath " + SRC_PATH
                            + " $(Get-Content sources.txt)"
            };
        } else {
            compileCommand = new String[] {
                    "/bin/bash", "-c",
                    "find " + SRC_PATH + " -name '*.java' > sources.txt; " +
                            "javac --release 16 -d " + CLASSES_DIR + " -sourcepath " + SRC_PATH + " @sources.txt"
            };
        }
        runCommand(compileCommand);
    }

    private void generateManifest(String mainClass) throws IOException {
        String manifestContent = "Manifest-Version: " + packageMetaData.version() + "\n" +
                "Main-Class: " + mainClass + "\n";
        Files.write(Paths.get(MANIFEST_PATH), manifestContent.getBytes(StandardCharsets.UTF_8));
    }

    private void createJar() throws IOException, InterruptedException {
        String[] jarCommand = {
                "jar", "cfm", jarFile.getAbsolutePath(), MANIFEST_PATH, "-C", CLASSES_DIR, "."
        };
        runCommand(jarCommand);
    }

    private void copyAdditionalFiles() throws IOException {
        Path srcPath = Paths.get(SRC_PATH);
        Path targetPath = Paths.get(CLASSES_DIR);
        try (Stream<Path> paths = Files.walk(srcPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> !path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            Path relativePath = srcPath.relativize(path);
                            Path destination = targetPath.resolve(relativePath);
                            Files.createDirectories(destination.getParent());
                            Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            throw new RuntimeException("Error copying file: " + path, e);
                        }
                    });
        }
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
            throw new RuntimeException("Error executing command: " + String.join(" ", command));
        }
    }

    public File getJarFile() {
        return jarFile;
    }
}
