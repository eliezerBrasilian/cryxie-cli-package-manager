package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.VSCodeSettingsManager;
import alpine.crixie.cli.utiities.contracts.dependency_downloader.JavaAllPackagesInstaller;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "deps-install",
        description = "Install all dependencies(packages) from package.lua"
)
public class InstallAllJavaPackagesCommand implements Runnable {

    @Override
    public void run() {
        try {
            new VSCodeSettingsManager();

            var deps = new PackageLuaModifier().getData().deps();

            deps.forEach(dependency -> {
                new JavaAllPackagesInstaller(dependency.name(), dependency.version()).download();
            });
            System.out.println("deps installed successfully");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
