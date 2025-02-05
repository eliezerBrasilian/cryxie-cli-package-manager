package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.GitIgnoreGenerator;
import alpine.crixie.cli.utiities.IntellijCryxieXmlManager;
import alpine.crixie.cli.utiities.VSCodeSettingsManager;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyDownloaderImpl;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "install",
        description = "Install a specified package."
)
public class InstallPackageCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName@version>", description = "Name and optional version of the package to be installed. Format: pacote[@version].")
    private String packageName;

    @Override
    public void run() {
        try {
            new VSCodeSettingsManager();
            new IntellijCryxieXmlManager();
            new GitIgnoreGenerator().generate();
            new DependencyDownloaderImpl(packageName).download();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
