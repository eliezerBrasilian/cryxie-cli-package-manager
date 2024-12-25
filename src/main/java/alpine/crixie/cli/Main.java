package alpine.java.mail;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        var _args = new String[]{"crixie", "install", "cebola"};

        int exitCode = new CommandLine(new RootCommand()).execute(args);
        System.exit(exitCode);
    }
}

@CommandLine.Command(
        name = "root",
        description = "Root Command for Crixie CLI",
        subcommands = {CrixieCommand.class}
)
class RootCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Use o comando 'crixie' seguido de uma ação. Exemplo: 'crixie install <pacote>'.");
    }
}

@CommandLine.Command(
        name = "crixie",
        description = "Crixie CLI - Gerenciador de pacotes e ferramentas",
        mixinStandardHelpOptions = true,
        version = "Crixie CLI 1.0.0",
        subcommands = {
                InstallCommand.class,
        }
)
class CrixieCommand implements Runnable {

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





