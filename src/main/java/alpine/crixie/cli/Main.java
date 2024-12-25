package alpine.crixie.cli;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {

        int exitCode = new CommandLine(new CliCommand()).execute(args);
        System.exit(exitCode);
    }
}

@CommandLine.Command(
        name = "crixie", //bat file
        description = "Crixie CLI - Gerenciador de pacotes e ferramentas",
        mixinStandardHelpOptions = true,
        version = "Crixie CLI 1.0.0",
        subcommands = {
                InstallCommand.class,
        }
)
class CliCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Bem-vindo ao Crixie CLI! Use --help para ver os comandos disponíveis.");
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
        // Aqui você pode implementar a lógica para instalar o pacote
    }
}





