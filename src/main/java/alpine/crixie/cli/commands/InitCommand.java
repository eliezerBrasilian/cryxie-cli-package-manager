package alpine.crixie.cli.commands;

import alpine.crixie.cli.components.PackageLuaComponent;
import picocli.CommandLine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

@CommandLine.Command(
        name = "init",
        description = "Initialize a project with the package.lua file"
)
public class InitCommand implements Runnable {

    @CommandLine.Option(
            names = {"-skip"},
            description = "Creates package.lua file with default values."
    )
    private boolean skip;

    @Override
    public void run() {
        if (skip) {
            new PackageLuaComponent();
        } else {
            generateCustomLuaComponent();
        }
        createCryxieLibsDirectory();
        System.out.println("Environment has been set up");
    }

    private static void createCryxieLibsDirectory(){
        String currentDir = System.getProperty("user.dir");

        var libsDirectory = new File(currentDir, "cryxie_libs");
        if (!libsDirectory.mkdirs()) {
            System.out.println("directory cryxie_libs couldn't be created");
        }
    }

    private static void generateCustomLuaComponent() {
        var scanner = new Scanner(System.in);
        System.out.print("Enter the package name: ");
        String packageName = scanner.nextLine();

        System.out.print("Enter the description: ");
        String desc = scanner.nextLine();

        System.out.print("Enter the version: ");
        String version = scanner.nextLine();

        System.out.print("Enter the url of your project repository : ");
        String repoUrl = scanner.nextLine();

        System.out.print("Do you want generate a full project?(Y)(N) : ");
        String generateProject = scanner.nextLine();

        new PackageLuaComponent(packageName,version,repoUrl,desc,generateProject);
    }

}
