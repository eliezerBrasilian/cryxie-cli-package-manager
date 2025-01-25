package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.FileNotFoundException;

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
        this.jarName = jarName.contains(".jar") ? jarName : jarName.concat(".jar");
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
    public void remove() throws FileNotFoundException {
        String jarFilePath = "cryxie_libs/" + jarName;
        File jarFile = new File(jarFilePath);

        boolean runtimeErrorHappened = false;

        do {
            try {
                if (jarFile.exists()) {

                    if (jarFile.delete()) {
                        System.out.println("File " + jarName + " was successfully deleted.");
                    } else {
                        throw new RuntimeException("Could not delete file (try again): " + jarName);
                    }
                }
            } catch (RuntimeException e) {
                runtimeErrorHappened = true;
            }
        } while (runtimeErrorHappened);
    }
}
