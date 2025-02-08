package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.JarGenerator_v2;
import alpine.crixie.cli.utiities.PackageLuaModifier;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "build",
        description = "build java project into a package."
)
public class BuildJavaPackageCommand implements Runnable {
    @Override
    public void run() {

        try {
            System.out.println("building package...");
            var generator = new JarGenerator_v2(new PackageLuaModifier());
            generator.generateJar();
            System.out.println("Package build completed successfully!");

        } catch (IOException | RuntimeException | InterruptedException e) {
            System.err.println("An error occurred during the build process: " + e.getMessage());
        }
    }
}
