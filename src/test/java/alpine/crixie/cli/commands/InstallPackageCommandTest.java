package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.contracts.dependency_downloader.DependencyDownloaderImpl;
import org.junit.jupiter.api.Test;

class InstallPackageCommandTest {

    @Test
    void run() {
        var packageName = "pacote2";
        new DependencyDownloaderImpl(packageName).download((s, r) -> {

        });
    }
}