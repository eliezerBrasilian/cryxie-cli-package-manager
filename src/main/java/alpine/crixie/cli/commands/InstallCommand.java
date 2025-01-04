package alpine.crixie.cli.commands;

import picocli.CommandLine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Comando: instalar pacotes
@CommandLine.Command(
        name = "install",
        description = "Install a specified package."
)
public class InstallCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName>", description = "Name of the package to be installed.")
    private String packageName;

    //1 - adicionar o jar ao projeto, compilar e tudo mais
    //2 - incluir o nome do projeto nas deps do package.lua

    @Override
    public void run() {
        System.out.println("Instalando o pacote: " + packageName);

        //supondo que o pacote foi baixado com sucesso dentro de cryxie_libs

    }
}
