package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.FileDownloader;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyManager;
import picocli.CommandLine;

import java.util.Scanner;

@CommandLine.Command(
        name = "install",
        description = "Install a specified package."
)
public class InstallCommand implements Runnable {

    @CommandLine.Parameters(paramLabel = "<packageName@version>", description = "Name and optional version of the package to be installed. Format: pacote[@version].")
    private String packageName;

    @Override
    public void run() {
        new DependencyManager(packageName).
                install((String name, String version) -> {
                    var scanner = new Scanner(System.in);

                    System.out.print("Enter the passcode: ");
                    String passcode = scanner.nextLine();
                    scanner.close();

                    new FileDownloader(name, version).retryDownload(passcode);
                });
    }

}
