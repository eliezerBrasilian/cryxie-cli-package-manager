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
