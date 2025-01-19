package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.JarGenerator;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "build",
        description = "build project into a package."
)
public class BuildJavaPackageCommand implements Runnable {
    @Override
    public void run() {

        try {
            JarGenerator generator = new JarGenerator();
            generator.generateJar();
            System.out.println("Package build completed successfully!");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred during the build process.", e);
        }
    }
}
