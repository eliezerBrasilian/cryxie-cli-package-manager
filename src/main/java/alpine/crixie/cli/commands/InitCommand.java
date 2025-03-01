package alpine.crixie.cli.commands;

import alpine.crixie.cli.components.PackageLuaComponent;
import alpine.crixie.cli.utiities.GitIgnoreGenerator;
import alpine.crixie.cli.utiities.ReadmeGenerator;
import alpine.crixie.cli.utiities.VSCodeSettingsManager;
import picocli.CommandLine;

import java.io.File;
import java.util.Scanner;

@CommandLine.Command(name = "init", description = "Initialize a project with the package.lua file")
public class InitCommand implements Runnable {

    @CommandLine.Option(names = { "-skip" }, description = "Creates package.lua file with default values.")
    private boolean skip;

    private static void createCryxieLibsDirectory() {
        String currentDir = System.getProperty("user.dir");

        var libsDirectory = new File(currentDir, "cryxie_libs");
        if (!libsDirectory.mkdirs()) {
            System.out.println("directory cryxie_libs couldn't be created");
        }
    }

    private static void generateCustomLuaComponent() {
        var scanner = new Scanner(System.in);

        System.out.print("Enter the name of project (e.g., my_project): ");
        String name = scanner.nextLine();

        System.out.print("Enter the package name (e.g., com.example.project): ");
        String packageName = scanner.nextLine();

        System.out.print("Enter the description: ");
        String desc = scanner.nextLine();

        System.out.print("Enter the version: ");
        String version = scanner.nextLine();

        System.out.print("Enter the url of your project repository : ");
        String repoUrl = scanner.nextLine();

        new PackageLuaComponent(name, packageName, version, repoUrl, desc);
    }

    @Override
    public void run() {
        if (skip) {
            new PackageLuaComponent();
        } else {
            generateCustomLuaComponent();
        }
        createCryxieLibsDirectory();
        new VSCodeSettingsManager();
        new GitIgnoreGenerator().generate();
        new ReadmeGenerator().generate();
        ;
        System.out.println("Environment has been set up");
    }

}
