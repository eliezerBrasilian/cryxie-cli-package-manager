package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.PackageLuaModifier;
import picocli.CommandLine;

import java.io.FileNotFoundException;

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
        System.out.println("Installing dependency: " + packageName);

        try {
            PackageLuaModifier modifier = PackageLuaModifier.getInstance();
            modifier.addDependency(packageName, "1.0.1");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //new PomXmlModifier().
        //        jarFileName("alpine_central_email_file_manager.jar")
          //      .add();

        //supondo que o pacote foi baixado com sucesso dentro de cryxie_libs

    }
}
