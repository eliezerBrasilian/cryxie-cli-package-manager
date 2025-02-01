package alpine.crixie.cli.utiities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static alpine.crixie.cli.utiities.Utils.LIB_PATH;
import static alpine.crixie.cli.utiities.Utils.SRC_PATH;

public class ProjectExecutor {
    private static final String BIN_PATH = "bin";
    // Obtém o separador correto para Windows (;) ou Linux/Mac (:)
    private final String PATH_SEPARATOR = System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":";
    private String PACKAGE_NAME = "";
    private final String MAIN_CLASS = PACKAGE_NAME;

    public ProjectExecutor(PackageLuaModifier packageLuaModifier) throws FileNotFoundException {
        PACKAGE_NAME = packageLuaModifier.getData().directoryWhereMainFileIs();
    }

    public boolean compileProject() throws IOException, InterruptedException {
        // Cria diretório bin se não existir
        Files.createDirectories(Paths.get(BIN_PATH));

        // Comando para compilar o projeto
        String compileCommand = String.format(
                "javac -cp \"%s/*\" -d %s %s/%s/*.java",
                LIB_PATH, BIN_PATH, SRC_PATH, PACKAGE_NAME.replace(".", "/")
        );

        return runCommand(compileCommand);
    }

    public boolean executeProject() throws IOException, InterruptedException {
        // Comando para rodar o programa compilado
        String executeCommand = String.format(
                "java -cp \"%s%s%s/*\" %s",
                BIN_PATH, PATH_SEPARATOR, LIB_PATH, MAIN_CLASS
        );

        return runCommand(executeCommand);
    }

    private boolean runCommand(String command) throws IOException, InterruptedException {
        System.out.println("🔹 Running: " + command);
        Process process = Runtime.getRuntime().exec(command);

        // Captura saída do comando
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        return exitCode == 0;
    }
}
