package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyDownloaderImpl;
import picocli.CommandLine;

import java.io.FileNotFoundException;

@CommandLine.Command(
        name = "uninstall",
        description = "Uninstall a specified package."
)
public class UninstallCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName@version>", description = "Name and optional version of the package to be uninstalled. Format: pacote[@version].")
    private String packageName;

    @Override
    public void run() {
        var ddi = new DependencyDownloaderImpl(packageName);

        try {
            //ddi.removeFromPomXmlFile();
            ddi.removePackageFromLuaFile();
            ddi.removePackageFromCryxieLibsDirectory();
        } catch (FileNotFoundException e) {
            System.err.print("Was not possible to uninstall completely the file: " + e.getMessage());
        }
    }

}
