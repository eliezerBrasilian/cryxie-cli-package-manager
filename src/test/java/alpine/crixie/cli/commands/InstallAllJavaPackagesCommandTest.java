package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.JavaAllPackagesInstaller;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

class InstallAllJavaPackagesCommandTest {

    @Test
    void run() throws FileNotFoundException {
        var deps = new PackageLuaModifier().getData().deps();

        deps.forEach(dependency -> {
            new JavaAllPackagesInstaller(dependency.name(), dependency.version()).download();
        });
        System.out.println("deps installed successfully");

    }
}