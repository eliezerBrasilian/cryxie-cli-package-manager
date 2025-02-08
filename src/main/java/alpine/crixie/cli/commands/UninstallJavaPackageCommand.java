package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.SearchArrumer;
import alpine.crixie.cli.utiities.contracts.DependencyManager;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyRemoverContract;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyRemoverContractImpl;
import picocli.CommandLine;

import java.io.FileNotFoundException;
import java.io.IOException;

@CommandLine.Command(
        name = "uninstall",
        description = "Uninstall a specified java package."
)
public class UninstallJavaPackageCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName@version>", description = "Name and optional version of the package to be uninstalled. Format: pacote[@version].")
    private String packageName;

    @Override
    public void run() {
        try {
            DependencyRemoverContract dependencyRemoverContract =
                    new DependencyRemoverContractImpl(new SearchArrumer(packageName));

            var manager = new DependencyManager(dependencyRemoverContract);
            manager.uninstallDependency();
        } catch (FileNotFoundException e) {
            System.err.print("Was not possible to uninstall completely the dependency: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
