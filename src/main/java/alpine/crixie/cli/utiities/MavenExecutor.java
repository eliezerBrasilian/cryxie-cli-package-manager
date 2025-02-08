package alpine.crixie.cli.utiities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

import static ch.qos.logback.core.testUtil.CoreTestConstants.TARGET_DIR;

public class MavenExecutor {
    private final String BUILD_DIR = "build";
    private final String MVN_COMMAND = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";

    private final PackageLuaModifier packageLuaModifier;

    public MavenExecutor(PackageLuaModifier packageLuaModifier) {
        this.packageLuaModifier = packageLuaModifier;
    }

    public void buildPackage() {
        try {
            if (executeMavenBuild()) {
                Optional<Path> generatedJar = findGeneratedJar();
                generatedJar.ifPresent(jarPath -> renameAndMoveJar(jarPath, packageLuaModifier.generateNameForBuiltPackage()));
                System.out.println("✅ Build completed successfully! The package has been moved to " + BUILD_DIR);
            } else {
                System.err.println("❌ Error on generating build.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error on generating build: " + e.getMessage());
        }
    }

    private boolean executeMavenBuild() throws IOException, InterruptedException {
        Process process = new ProcessBuilder(MVN_COMMAND, "clean", "package")
                .redirectErrorStream(true)
                .start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        return process.waitFor() == 0;
    }

    private Optional<Path> findGeneratedJar() throws IOException {
        try (Stream<Path> files = Files.list(Paths.get(TARGET_DIR))) {
            return files
                    .filter(path -> path.toString().endsWith(".jar"))
                    .findFirst();
        }
    }

    private void renameAndMoveJar(Path jarPath, String newJarName) {
        try {
            Files.createDirectories(Paths.get(BUILD_DIR));
            Path destination = Paths.get(BUILD_DIR, newJarName);
            Files.move(jarPath, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ JAR moved to: " + destination);
        } catch (IOException e) {
            System.err.println("❌ Error on moving: " + e.getMessage());
        }
    }

    private void moveJarToBuild() throws IOException {
        Files.createDirectories(Paths.get(BUILD_DIR));

        // Localiza o JAR gerado
        Optional<Path> jarFile = Files.walk(Paths.get("target"))
                .filter(p -> p.toString().endsWith(".jar") && !p.toString().contains("original"))
                .findFirst();

        if (jarFile.isPresent()) {
            Path source = jarFile.get();
            Path target = Paths.get(BUILD_DIR, source.getFileName().toString());
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } else {
            System.err.println("❌ No JAR found in target folder.");
        }
    }
}
