package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainClassGenerator {
    private final String BASE_PATH = "src".concat(File.separator).concat("main").concat(File.separator).concat("java").concat(File.separator);

    public void generate(String packageName) {
        generate(packageName, directory -> {
        });
    }

    public void generate(String packageName, CreationCallback callback) {
        if (packageName == null || packageName.isBlank()) {
            System.err.println("Package name cannot be empty.");
            return;
        }

        // Convertendo o nome do pacote em um caminho de diretórios
        String packagePath = BASE_PATH + packageName.replace(".", File.separator);
        File directory = new File(System.getProperty("user.dir"), packagePath);

        // Criando diretórios, se necessário
        if (directory.mkdirs()) {
            System.out.println("Directory structure created at: " + directory.getAbsolutePath());
        } else if (!directory.exists()) {
            System.err.println("Failed to create directory structure.");
            return;
        }

        callback.onSuccess(directory);
        createMainClass(directory, packageName);
    }

    private void createMainClass(File directory, String packageName) {
        File mainJavaFile = new File(directory, "Main.java");

        String mainClassContent = """
                package %s;
                
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("This project was created with Cryxie, yay 😊😁👌!");
                    }
                }
                """.formatted(packageName);

        try (FileWriter writer = new FileWriter(mainJavaFile)) {
            writer.write(mainClassContent);
            System.out.println("Main.java file created at: " + mainJavaFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error creating Main.java file: " + e.getMessage());
        }
    }

    @FunctionalInterface
    public interface CreationCallback {
        void onSuccess(File directory);
    }
}
