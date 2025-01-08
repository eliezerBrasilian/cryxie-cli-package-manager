package alpine.crixie.cli.utiities.contracts.dependency_downloader;

import alpine.crixie.cli.utiities.FileDownloader;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

class DependencyManagerTest {

    final String packageName = "batatafrita";
    
    @Test
    void install() {
        new DependencyManager(packageName).
                install((String name, String version) -> {
                    var scanner = new Scanner(System.in);

                    System.out.print("Enter the passcode: ");
                    String passcode = scanner.nextLine();

                    new FileDownloader(name, version).retryDownload(passcode);
                });
    }

    @Test
    void uninstall() {
        new DependencyManager(packageName).uninstall();
    }
}