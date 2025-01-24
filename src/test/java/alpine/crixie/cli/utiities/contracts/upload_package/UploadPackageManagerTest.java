package alpine.crixie.cli.utiities.contracts.upload_package;

import alpine.crixie.cli.mocks.Mocks;
import alpine.crixie.cli.utiities.PromptPackageDownloader;
import alpine.crixie.cli.utiities.requests.PackageRequest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class UploadPackageManagerTest {

    @Test
    void downloadPackage2() {
        new PromptPackageDownloader("pacote2", "1.0.0")
                .downloadPackage();
    }

    //enviando pacote 2 com pacote como dependencia
    @Test
    void sendPackage2() throws IOException, InterruptedException {
        var readmeFile = new File(System.getProperty("user.dir") + "/README.md");
        var jarFile = new File(System.getProperty("user.dir") + "/projeto2.jar");

        new PackageRequest().send(Mocks.package2(), readmeFile, jarFile);
    }

    //enviando pacote 1 sem dependencias
    @Test
    void sendPackage() throws IOException, InterruptedException {
        var readmeFile = new File(System.getProperty("user.dir") + "/README.md");
        var jarFile = new File(System.getProperty("user.dir") + "/projeto1.jar");

        new PackageRequest().
                send(Mocks.package1(), readmeFile, jarFile);
    }
}