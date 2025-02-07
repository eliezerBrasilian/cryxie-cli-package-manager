package alpine.crixie.cli.utiities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static alpine.crixie.cli.utiities.Utils.LIB_PATH;
import static alpine.crixie.cli.utiities.Utils.SRC_PATH;

public class ProjectExecutor {
    private static final String BIN_PATH = "bin";
    private final String PATH_SEPARATOR = System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":";
    private final String PACKAGE_NAME;
    private final String MAIN_CLASS;

    public ProjectExecutor(PackageLuaModifier packageLuaModifier) throws FileNotFoundException {
        PACKAGE_NAME = packageLuaModifier.getData().directoryWhereMainFileIs()
                .replace("src.main.java.", ""); // Remove o prefixo errado
        MAIN_CLASS = PACKAGE_NAME;
    }

    public boolean compileProject() throws IOException, InterruptedException {
        Files.createDirectories(Paths.get(BIN_PATH));

        String classpath = expandClasspath(LIB_PATH) + PATH_SEPARATOR + SRC_PATH;
        String compileCommand = String.format(
                "javac -cp \"%s\" -d %s %s/%s.java",
                classpath, BIN_PATH, SRC_PATH, PACKAGE_NAME.replace(".", File.separator)
        );

        return runCommand(compileCommand);
    }

    public boolean executeProject() throws IOException, InterruptedException {
        String classpath = BIN_PATH + PATH_SEPARATOR + expandClasspath(LIB_PATH);
        String executeCommand = String.format(
                "java -cp \"%s\" %s",
                classpath, MAIN_CLASS
        );

        return runCommand(executeCommand);
    }

    private String expandClasspath(String libPath) {
        File libDir = new File(libPath);
        if (!libDir.exists() || !libDir.isDirectory()) {
            return libPath;
        }

        StringBuilder classpath = new StringBuilder();
        for (File file : libDir.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                if (!classpath.isEmpty()) {
                    classpath.append(PATH_SEPARATOR);
                }
                classpath.append(file.getAbsolutePath());
            }
        }

        return classpath.toString();
    }

    private boolean runCommand(String command) throws IOException, InterruptedException {
        System.out.println("🔹 Running: " + command);

        Process process;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // No Windows, execute o comando diretamente
            process = Runtime.getRuntime().exec(command);
        } else {
            // No Linux, use /bin/bash para executar o comando
            String[] shellCommand = {"/bin/bash", "-c", command};
            process = Runtime.getRuntime().exec(shellCommand);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
        }

        int exitCode = process.waitFor();
        return exitCode == 0;
    }
}
