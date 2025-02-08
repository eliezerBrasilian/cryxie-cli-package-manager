package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.contracts.DependencyManager;
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
            new DependencyManager().installDependenciesFromLuaPackageFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
