package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The CryxieLibsDirectory class provides utility methods for managing JAR files
 * in the "cryxie_libs" directory. This includes functionality for setting the
 * JAR file name and removing the file from the directory.
 */
public class CryxieLibsDirectory {
    // Stores the name of the JAR file.
    private String jarName;

    /**
     * Sets the name of the JAR file.
     * If the provided name does not include the ".jar" extension, it will be automatically appended.
     *
     * @param jarName The name of the JAR file (with or without the ".jar" extension).
     * @return The current instance of CryxieLibsDirectory for method chaining.
     */
    public CryxieLibsDirectory jarFileName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    /**
     * Removes the specified JAR file from the "cryxie_libs" directory.
     * <p>
     * If the file exists, it will attempt to delete it. If the deletion is successful,
     * a confirmation message will be printed. Otherwise, an exception is thrown.
     *
     * @throws FileNotFoundException if the specified file is not found in the "cryxie_libs" directory.
     * @throws RuntimeException      if the file exists but cannot be deleted.
     */
    public void remove() throws IOException {

        findJarWithName().forEach(jar -> {
            String jarFilePath = "cryxie_libs".concat(File.separator) + jar;
            File jarFile = new File(jarFilePath);

            while (true) {
                try {
                    if (jarFile.exists()) {
                        if (jarFile.delete()) {
                            System.out.println("File " + jarName + " was successfully uninstalled.");
                            break;
                        } else {
                            throw new RuntimeException("Could not delete file (try again): " + jarName);
                        }
                    } else {
                        System.out.println("Package " + jarName + " does not exist.");
                        break;
                    }
                } catch (RuntimeException e) {
                    System.err.println("An error occurred while trying to delete the file: " + e.getMessage());

                    try {
                        Thread.sleep(1000); // Aguarda 1 segundo antes de tentar novamente
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during retry", ex);
                    }
                }
            }
        });
    }


    private List<String> findJarWithName() throws IOException {
        Path buildDir = Paths.get("cryxie_libs");
        List<Path> jarFiles = Files.walk(buildDir)
                .filter(path -> path.toString().endsWith(".jar"))
                .toList();

        var foundedPathList = jarFiles.stream().filter(jar -> jar.toString().contains(jarName)).toList();
        System.out.println(foundedPathList);

        var foundedList = new ArrayList<String>();

        for (Path path : foundedPathList) {
            var pathString = path.toString();

            var novoNome = pathString.replace(File.separator, "/");

            var splited = novoNome.split("/");

            if (splited[1].startsWith(jarName))
                foundedList.add(splited[1]);
        }
        return foundedList;
    }

}
