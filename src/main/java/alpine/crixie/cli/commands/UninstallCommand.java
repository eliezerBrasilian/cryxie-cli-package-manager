package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyManager;
import picocli.CommandLine;

@CommandLine.Command(
        name = "uninstall",
        description = "Uninstall a specified package."
)
public class UninstallCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName@version>", description = "Name and optional version of the package to be uninstalled. Format: pacote[@version].")
    private String packageName;

    @Override
    public void run() {
        new DependencyManager(packageName).uninstall();
    }

}
