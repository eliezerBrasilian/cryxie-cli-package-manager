package alpine.crixie.cli;

import alpine.crixie.cli.commands.*;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {

        @CommandLine.Command(
                name = "cryxie", //bat file
                //name = "teste",
                description = "Crixie CLI - User Friendly Java Package Manager",
                mixinStandardHelpOptions = true,
                version = "1.0.0",
                subcommands = {
                        InstallPackageCommand.class,
                        InitCommand.class,
                        CommandLine.HelpCommand.class,
                        UninstallCommand.class,
                        BuildAndUploadJavaPackageCommand.class,
                        BuildJavaPackageCommand.class,
                        UploadNewJavaVersionPackageCommand.class,
                        InstallAllJavaPackagesCommand.class,
                        UploadJavaPackageCommand.class,
                        RunJavaPackageCommand.class
                }
        )
        class CliCommand implements Runnable {

            @Override
            public void run() {
                System.out.println("Welcome to Crixie CLI! Use --help to see available commands.");
            }
        }

        int exitCode = new CommandLine(new CliCommand()).execute(args);
        System.exit(exitCode);
    }
}