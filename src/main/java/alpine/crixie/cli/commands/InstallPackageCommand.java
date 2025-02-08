package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.SearchArrumer;
import alpine.crixie.cli.utiities.contracts.DependencyManager;
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
            var manager = new DependencyManager(new SearchArrumer(packageName));
            manager.installDependencyFromPrompt();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
