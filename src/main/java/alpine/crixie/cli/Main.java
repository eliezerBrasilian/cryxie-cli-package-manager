package alpine.crixie.cli;

import picocli.CommandLine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        int exitCode = new CommandLine(new CliCommand()).execute(args);
        System.exit(exitCode);
    }
}

@CommandLine.Command(
        //name = "cryxie", //bat file
        name = "teste",
        description = "Crixie CLI - Gerenciador de pacotes e ferramentas",
        mixinStandardHelpOptions = true,
        version = "Crixie CLI 1.0.0",
        subcommands = {
                InstallCommand.class,
                InitCommand.class,
                CommandLine.HelpCommand.class // Subcomando para exibir ajuda de outros comandos
        }
)
class CliCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Welcome to Crixie CLI! Use --help to see available commands.");
    }
}

@CommandLine.Command(
        name = "init",
        description = "Initialize a project with the package.lua file"
)
class InitCommand implements Runnable {

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



// Comando: instalar pacotes
@CommandLine.Command(
        name = "install",
        description = "Install a specified package."
)
class InstallCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName>", description = "Name of the package to be installed.")
    private String packageName;

    @Override
    public void run() {
        System.out.println("Instalando o pacote: " + packageName);
        // Obtém o diretório atual
        String currentDir = System.getProperty("user.dir");
        File newFile = new File(currentDir, packageName + ".txt");

        // Cria o arquivo correspondente
        try {
            if (newFile.createNewFile()) {
                System.out.println("O arquivo '" + newFile.getName() + "' foi criado no diretório atual: " + currentDir);
                // Escreve algo no arquivo, se necessário
                try (FileWriter writer = new FileWriter(newFile)) {
                    writer.write("---\n" +
                            "--- Config related to your package in cryxie\n" +
                            "---\n" +
                            "\n" +
                            "Name = \"pacote\"\n" +
                            "Version = \"0.0.1\"\n");
                }
            } else {
                System.out.println("O arquivo '" + newFile.getName() + "' já existe no diretório atual: " + currentDir);
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo: " + e.getMessage());
        }
    }
}





