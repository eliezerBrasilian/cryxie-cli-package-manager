package alpine.crixie.cli.utiities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GitIgnoreGenerator {
    public void generate() {
        var gitIgnoreFile = new File(System.getProperty("user.dir"), ".gitignore");

        try {
            if (!gitIgnoreFile.exists()) {
                if (gitIgnoreFile.createNewFile()) {
                    try (FileWriter writer = new FileWriter(gitIgnoreFile)) {

                        String defaultContent =
                                """
                                        cryxie.bat
                                        cryxie_libs
                                        sources.txt
                                        build
                                        bin
                                        local_storage.lua
                                        """;


                        writer.write(defaultContent);

                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error on creating .gitignore file: " + e.getMessage());
        }

    }
}
