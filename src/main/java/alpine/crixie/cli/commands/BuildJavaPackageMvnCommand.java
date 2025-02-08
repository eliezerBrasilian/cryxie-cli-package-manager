package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.MavenExecutor;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "build-maven",
        description = "build java project into a package with maven."
)
public class BuildJavaPackageMvnCommand implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println("🔹 Building java package...");
            var generator = new MavenExecutor(new PackageLuaModifier());
            generator.buildPackage();
        } catch (IOException | RuntimeException e) {
            System.err.println("An error occurred during the build process: " + e.getMessage());
        }
    }
}
