package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyManager;
import picocli.CommandLine;

@CommandLine.Command(
        name = "install",
        description = "Install a specified package."
)
public class InstallCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName>", description = "Name of the package to be installed.")
    private String packageName;

    @Override
    public void run() {
        new DependencyManager(packageName).execute();
    }
}
