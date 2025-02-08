package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class JarSelector {
    public Optional<File> select() throws IOException {
        Path buildDir = Paths.get("build");

        List<Path> jarFiles = Files.walk(buildDir)
                .filter(path -> path.toString().endsWith(".jar"))
                .toList();

        if (jarFiles.isEmpty()) {
            System.out.println("No .jar files found in the build directory.");
            return Optional.empty();
        }

        System.out.println("Select the .jar file to upload:");
        for (int i = 0; i < jarFiles.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, jarFiles.get(i).getFileName());
        }

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice < 1 || choice > jarFiles.size()) {
            System.out.print("Enter the number corresponding to your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                scanner.next(); // Clear invalid input
            }
        }

        Path selectedJar = jarFiles.get(choice - 1);
        System.out.printf("You selected: %s%n", selectedJar.getFileName());

        return Optional.of(selectedJar.toFile());
    }
}
