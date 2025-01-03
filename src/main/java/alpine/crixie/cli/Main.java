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
       // name = "cryxie", //bat file
        name = "teste",
        description = "Crixie CLI - Gerenciador de pacotes e ferramentas",
        mixinStandardHelpOptions = true,
        version = "Crixie CLI 1.0.0",
        subcommands = {
                InstallCommand.class,
                InitCommand.class
        }
)
class CliCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Bem-vindo ao Crixie CLI! Use --help para ver os comandos disponíveis.");
    }
}

@CommandLine.Command(
        name = "init",
        description = "Inicializa um projeto com o arquivo package.txt"
)
class InitCommand implements Runnable {

    @CommandLine.Option(
            names = {"-skip"},
            description = "Cria o arquivo package.txt com valores padrão."
    )
    private boolean skip;

    @Override
    public void run() {
        String currentDir = System.getProperty("user.dir");
        File newFile = new File(currentDir, "package.lua");

        try {
            if (newFile.createNewFile()) {
                System.out.println("O arquivo '" + newFile.getName() + "' foi criado no diretório atual: " + currentDir);

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
                        System.out.println("Arquivo criado com configurações padrão.");
                    } else {
                        // Solicita informações ao usuário
                        Scanner scanner = new Scanner(System.in);
                        System.out.print("Informe o nome do pacote: ");
                        String packageName = scanner.nextLine();
                        System.out.print("Informe a versão: ");
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
                        System.out.println("Arquivo criado com as configurações fornecidas.");
                    }
                }
            } else {
                System.out.println("O arquivo '" + newFile.getName() + "' já existe no diretório atual: " + currentDir);
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo: " + e.getMessage());
        }

    }
}



// Comando: instalar pacotes
@CommandLine.Command(
        name = "install",
        description = "Instalar um pacote especificado."
)
class InstallCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName>", description = "Nome do pacote a ser instalado.")
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





