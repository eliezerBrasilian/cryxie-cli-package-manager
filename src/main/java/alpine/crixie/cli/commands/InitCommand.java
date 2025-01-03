package alpine.crixie.cli.commands;

import picocli.CommandLine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

@CommandLine.Command(
        name = "init",
        description = "Initialize a project with the package.lua file"
)
public class InitCommand implements Runnable {

    @CommandLine.Option(
            names = {"-skip"},
            description = "Creates package.lua file with default values."
    )
    private boolean skip;

    @Override
    public void run() {
        String currentDir = System.getProperty("user.dir");
        File newFile = new File(currentDir, "package.lua");

        try {
            if (newFile.createNewFile()) {
                // Escrever dados no arquivo
                try (FileWriter writer = new FileWriter(newFile)) {
                    if (skip) {
                        // Conteúdo padrão
                        writer.write("---\n" +
                                "--- Config related to your package in cryxie\n" +
                                "---\n" +
                                "\n" +
                                "Name = \"default\"\n" +
                                "Version = \"0.0.1\"\n");
                        System.out.println("Environment has been set up");
                    } else {
                        // Solicita informações ao usuário
                        Scanner scanner = new Scanner(System.in);
                        System.out.print("Enter the package name: ");
                        String packageName = scanner.nextLine();
                        System.out.print("Enter the version: ");
                        String version = scanner.nextLine();

                        // Escreve as informações fornecidas no arquivo
                        writer.write("---\n" +
                                "--- Config related to your package in cryxie\n" +
                                "---\n" +
                                "\n" +
                                "Name = \"" + packageName + "\"\n" +
                                "Version = \"" + version + "\"\n" +
                                "\n" +
                                "Dependencies = {}");
                        System.out.println("Environment has been set up");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("error setting up environment: " + e.getMessage());
        }

    }
}
