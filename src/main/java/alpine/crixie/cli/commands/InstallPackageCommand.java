package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyDownloaderImpl;
import picocli.CommandLine;

@CommandLine.Command(
        name = "install",
        description = "Install a specified package."
)
public class InstallPackageCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName@version>", description = "Name and optional version of the package to be installed. Format: pacote[@version].")
    private String packageName;

    @Override
    public void run() {

        new DependencyDownloaderImpl(packageName).download();
    }

}
