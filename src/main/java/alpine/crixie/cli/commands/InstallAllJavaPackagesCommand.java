package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.JavaAllPackagesInstaller;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import picocli.CommandLine;

import java.io.FileNotFoundException;

@CommandLine.Command(
        name = "deps-install",
        description = "Install all dependencies(packages) from package.lua"
)
public class InstallAllJavaPackagesCommand implements Runnable {

    @Override
    public void run() {
        try {
            var deps = new PackageLuaModifier().getData().deps();

            deps.forEach(dependency -> {
                new JavaAllPackagesInstaller(dependency.name(), dependency.version()).download();
            });
            System.out.println("deps installed successfully");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
