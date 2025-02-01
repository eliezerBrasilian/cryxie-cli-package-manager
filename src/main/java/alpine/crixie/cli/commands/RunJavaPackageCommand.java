package alpine.crixie.cli.commands;

import alpine.crixie.cli.utiities.PackageLuaModifier;
import alpine.crixie.cli.utiities.ProjectExecutor;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "run",
        description = "run the java project"
)
public class RunJavaPackageCommand implements Runnable {
    @Override
    public void run() {
        try {
            var projectExecutor = new ProjectExecutor(new PackageLuaModifier());
            if (projectExecutor.compileProject()) {
                projectExecutor.executeProject();
            } else {
                System.err.println("❌ Compilation error. Aborting execution.");
            }
        } catch (IOException | RuntimeException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
