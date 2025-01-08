package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.FileNotFoundException;

public class CryxieLibsDirectory {
    private String jarName;

    public CryxieLibsDirectory jarFileName(String jarName) {
        this.jarName = jarName.contains(".jar") ? jarName : jarName.concat(".jar");
        return this;
    }

    public void remove() throws FileNotFoundException {
        String jarFilePath = "cryxie_libs/" + jarName;
        File jarFile = new File(jarFilePath);

        if (jarFile.exists()) {
            if (jarFile.delete()) {
                System.out.println("File " + jarName + " was successfully deleted.");
            } else {
                throw new RuntimeException("Could not delete file (try again): " + jarName);
            }
        } else {
            throw new FileNotFoundException("File " + jarName + " not found in cryxie_libs directory.");
        }
    }
}
